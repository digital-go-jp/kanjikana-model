/*
 * MIT License
 *
 * Copyright (c) 2024 デジタル庁
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.go.digital.kanjikana.core.engine.ai.generate;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.ParameterStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 学習済みモデルからビームサーチによる推論を行う (ビーム一括バッチ推論版)
 */
public final class BeamSearch extends AbstSearch implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(BeamSearch.class);

    //private final BeamCache cache;

    /** 保持のみ。ビームをバッチ次元にまとめるため、Java 側スレッド並列は使用しない */
    private final boolean parallelEnabled;

    private final ParameterStore ps = new ParameterStore();

    public BeamSearch(GenerateEngine aimodels) throws Exception {
        super(aimodels);
        //this.cache = BeamCache.newInstance();
        this.parallelEnabled = this.search_params.isParallel_enabled();
        logger.info("BeamSearch initialized (batched beams). parallelEnabled(ignored)={}", this.parallelEnabled);
    }

    public BeamSearch(GenerateEngine aimodels, boolean parallelEnabled) throws Exception {
        super(aimodels);
        //this.cache = BeamCache.newInstance();
        this.parallelEnabled = parallelEnabled;
        logger.info("BeamSearch initialized (batched beams). parallelEnabled(ignored)={}", this.parallelEnabled);
    }

    /**
     * 旧コンストラクタ（明示並列度指定）— バッチ推論化により parallelism は無視される。
     */
    @Deprecated
    public BeamSearch(GenerateEngine aimodels, boolean parallelEnabled, int parallelism) throws Exception {
        this(aimodels, parallelEnabled);
    }

    /**
     * ビームサーチで使用する 1 ノード
     */
    private static final class BeamSearchNode {
        private final BeamSearchNode prevNode;
        private final long decoder_input;
        private final double logProb;
        private final int length;
        private final double eval;

        private BeamSearchNode(BeamSearchNode prevNode, long decoder_input, double logProb, int length) {
            this(prevNode, decoder_input, logProb, length, 0.6);
        }

        private BeamSearchNode(BeamSearchNode prevNode, long decoder_input, double logProb, int length, double alpha) {
            this.prevNode = prevNode;
            this.decoder_input = decoder_input;
            this.logProb = logProb;
            this.length = length;
            this.eval = -this.logProb / Math.pow(((5 + this.length) / (5. + 1.)), alpha);
        }

        private double eval() {
            return this.eval;
        }
    }

    private List<Long> concat_input(BeamSearchNode n) {
        List<Long> y = new ArrayList<>();
        while (n != null) {
            y.add(n.decoder_input);
            n = n.prevNode;
        }
        Collections.reverse(y);
        return y;
    }

    @Override
    public List<SearchResult> run(String src) {
        //List<SearchResult> cached = cache.get(src);
        //if (cached != null) {
        //    return cached;
        //}
        List<SearchResult> res = run(search_params.getMax_len(),
                search_params.getBeam_width(),
                search_params.getN_best(),
                src);
        //cache.put(src, res);
        return res;
    }

    public List<SearchResult> run(int beam_width, int n_best, String src){
        return run(search_params.getMax_len(), beam_width, n_best, src);
    }

    public List<SearchResult> run(int max_len, int beam_width, int n_best, String src){

        List<SearchResult> res = new ArrayList<>();
        logger.debug("src={}", src);

        if (src == null || src.isEmpty()) {
            return res;
        }

        long bosId = aimodels.getVocab_tgt().getWord2Index().get(GenerateEngine.BOS);
        long eosId = aimodels.getVocab_tgt().getWord2Index().get(GenerateEngine.EOS);

        NDList enc = encode(src);
        List<BeamSearchNode> best_nodes = new ArrayList<>();
        try {
            best_nodes.add(new BeamSearchNode(null, bosId, 0, 0));

            for (int loop = 0; loop < max_len; loop++) {
                List<BeamSearchNode> expandable = new ArrayList<>(best_nodes.size());
                List<BeamSearchNode> cand_nodes = new ArrayList<>();
                for (BeamSearchNode n : best_nodes) {
                    if (n.decoder_input == eosId) {
                        cand_nodes.add(n);
                    } else {
                        expandable.add(n);
                    }
                }
                if (expandable.isEmpty()) {
                    break;
                }

                // 全ビームを 1 回の decoder forward にまとめる
                cand_nodes.addAll(expandBatch(expandable, enc, beam_width));

                cand_nodes.sort(Comparator.comparingDouble(BeamSearchNode::eval));
                best_nodes = new ArrayList<>(
                        cand_nodes.subList(0, Math.min(beam_width, cand_nodes.size())));
            }
        } finally {
            enc.close();
        }

        best_nodes.sort(Comparator.comparingDouble(BeamSearchNode::eval));
        if (best_nodes.size() > n_best) {
            best_nodes = best_nodes.subList(0, n_best);
        }

        for (BeamSearchNode n : best_nodes) {
            double prob = Math.exp(n.logProb); // 0-1へ変換 v2.0
            List<Long> token = new ArrayList<>();
            BeamSearchNode cur = n;
            while (cur != null) {
                token.add(cur.decoder_input);
                cur = cur.prevNode;
            }
            Collections.reverse(token);

            StringBuilder sb = new StringBuilder();
            for (Long t : token) {
                String w = aimodels.getVocab_tgt().getIndex2Word().get(t);
                if (w.equals(GenerateEngine.EOS) || w.equals(GenerateEngine.BOS)) {
                    continue;
                }
                sb.append(w);
            }
            logger.debug("candidate={} prob={}", sb, prob);
            res.add(new SearchResult(sb.toString(), prob));
        }

        return res;
    }

    /**
     * 展開対象のビームをバッチ次元にまとめ、decoder / generator を 1 回だけ呼び出す。
     * デコーダ入力は (系列長, バッチ) レイアウト。
     */
    private List<BeamSearchNode> expandBatch(List<BeamSearchNode> nodes, NDList enc, int beam_width) {
        int batch = nodes.size();
        int len = nodes.get(0).length + 1; // BOS を含むトークン数（展開対象は全て同じ長さ）
        long[] flat = new long[len * batch];
        for (int i = 0; i < batch; i++) {
            List<Long> ys = concat_input(nodes.get(i));
            for (int s = 0; s < len; s++) {
                flat[s * batch + i] = ys.get(s);
            }
        }

        long[] words;
        float[] logProbs;
        try (NDManager scope = manager.newSubManager()) {
            NDArray inputTensor = scope.create(flat, new Shape(len, batch));

            NDList tgt_emb = aimodels.getTgt_tok_emb().getBlock().forward(ps, new NDList(inputTensor), false);
            NDList tgt_pos = aimodels.getPositional_encoding().getBlock().forward(ps, tgt_emb, false);

            NDArray memory = enc.get(0);
            if (batch > 1) {
                memory = memory.repeat(1, batch);
                memory.attach(scope);
            }

            NDList tgt = new NDList(tgt_pos);
            tgt.add(memory);
            tgt.addAll(generate_square_subsequent_mask(len));

            NDList dec = aimodels.getDecoder().getBlock().forward(ps, tgt, false);
            NDArray lastStep = dec.get(0).get(len - 1); // (batch, emb)

            NDList prob = aimodels.getGenerator().getBlock().forward(ps, new NDList(lastStep), false);
            NDArray logSoftmaxed = prob.get(0).logSoftmax(1);

            NDList top = logSoftmaxed.topK(beam_width, 1);
            words = top.get(1).toLongArray();     // (batch, beam_width) を行優先で取得
            logProbs = top.get(0).toFloatArray();
        }

        List<BeamSearchNode> newNodes = new ArrayList<>(batch * beam_width);
        for (int i = 0; i < batch; i++) {
            BeamSearchNode node = nodes.get(i);
            for (int j = 0; j < beam_width; j++) {
                int k = i * beam_width + j;
                newNodes.add(new BeamSearchNode(
                        node,
                        words[k],
                        node.logProb + logProbs[k],
                        node.length + 1));
            }
        }
        return newNodes;
    }

    @Override
    public void close() {
        super.close();
    }

}