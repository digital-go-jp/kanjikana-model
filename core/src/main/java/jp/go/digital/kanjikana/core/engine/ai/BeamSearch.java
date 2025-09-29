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

package jp.go.digital.kanjikana.core.engine.ai;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.ParameterStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 学習済みモデルからビームサーチによる推論を行う (並列対応版, beam_width 一般運用)
 */
public final class BeamSearch extends AbstSearch implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(BeamSearch.class);

    private final BeamCache cache;

    // 並列実行制御
    private final boolean parallelEnabled;
    private final int parallelism;
    private final ExecutorService executor;

    // 一般的な beam_width 戦略
    private final boolean legacySelectEachLoop = false;

    private final ThreadLocal<ParameterStore> threadLocalPS = ThreadLocal.withInitial(ParameterStore::new);
    private final ThreadLocal<ai.djl.ndarray.NDManager> threadLocalMgr =
            ThreadLocal.withInitial(() -> this.manager.newSubManager());

    /**
     * 自動並列設定コンストラクタ
     * CPUコア数 (logical) を基準に並列度を自動決定。
     * システムプロパティ 'kanjikana.beam.parallelism' が指定されていればそれを優先。
     */
    public BeamSearch(AiModels aimodels) throws Exception {
        super(aimodels);
        this.cache = BeamCache.newInstance();
        int parallelism = this.search_params.getParallelism();
        this.parallelEnabled = this.search_params.isParallel_enabled();
        if(this.search_params.getParallelism()==0){
            this.parallelism = parallelEnabled ? detectParallelism() : 1;
            this.executor = parallelEnabled
                    ? Executors.newFixedThreadPool(this.parallelism,
                    r -> {
                        Thread t = new Thread(r, "BeamSearchWorker");
                        t.setDaemon(true);
                        return t;
                    })
                    : null;
        }else{
            this.parallelism = parallelEnabled ? Math.max(1, parallelism) : 1;
            this.executor = parallelEnabled
                    ? Executors.newFixedThreadPool(this.parallelism,
                    r -> {
                        Thread t = new Thread(r, "BeamSearchWorker");
                        t.setDaemon(true);
                        return t;
                    })
                    : null;
        }

        logger.info("BeamSearch (propaties) initialized. parallelEnabled={}, parallelism(auto)={}, legacySelectEachLoop={}",
                this.parallelEnabled, this.parallelism, this.legacySelectEachLoop);
    }

    /**
     * 並列化ON/OFFを指定できる簡易コンストラクタ（並列度は自動検出）
     */
    public BeamSearch(AiModels aimodels,boolean parallelEnabled) throws Exception {
        super(aimodels);
        this.cache = BeamCache.newInstance();
        this.parallelEnabled = parallelEnabled;
        this.parallelism = parallelEnabled ? detectParallelism() : 1;
        this.executor = parallelEnabled
                ? Executors.newFixedThreadPool(this.parallelism,
                r -> {
                    Thread t = new Thread(r, "BeamSearchWorker");
                    t.setDaemon(true);
                    return t;
                })
                : null;
        logger.info("BeamSearch initialized. parallelEnabled={}, parallelism(auto)={}, legacySelectEachLoop={}",
                this.parallelEnabled, this.parallelism, this.legacySelectEachLoop);
    }

    /**
     * 旧コンストラクタ（明示並列度指定）— 自動設定推奨のため使用非推奨。
     * 残しておくが、内部的にも detectParallelism を利用する選択肢をコメントで提示。
     */
    @Deprecated
    public BeamSearch(AiModels aimodels,boolean parallelEnabled, int parallelism) throws Exception {
        super(aimodels);
        this.cache = BeamCache.newInstance();
        this.parallelEnabled = parallelEnabled;
        this.parallelism = parallelEnabled ? Math.max(1, parallelism) : 1;
        // もし渡された parallelism <= 0 の場合は自動検出にしたいなら下記を利用:
        // this.parallelism = parallelEnabled ? (parallelism > 0 ? parallelism : detectParallelism()) : 1;
        this.executor = parallelEnabled
                ? Executors.newFixedThreadPool(this.parallelism,
                r -> {
                    Thread t = new Thread(r, "BeamSearchWorker");
                    t.setDaemon(true);
                    return t;
                })
                : null;
        logger.info("BeamSearch (deprecated ctor) initialized. parallelEnabled={}, parallelism={}, legacySelectEachLoop={}",
                this.parallelEnabled, this.parallelism, this.legacySelectEachLoop);
    }

    /**
     * 並列度自動検出ロジック。
     * 優先順位:
     *   1. System Property: -Dkanjikana.beam.parallelism=N
     *   2. Runtime.getRuntime().availableProcessors()
     *   3. フォールバック 1
     */
    private static int detectParallelism() {
        int fallback = 1;
        try {
            String prop = System.getProperty("kanjikana.beam.parallelism");
            int cores = Runtime.getRuntime().availableProcessors();
            if (prop != null && !prop.isBlank()) {
                int specified = Integer.parseInt(prop.trim());
                if (specified > 0) {
                    // 上限を cores に合わせたいなら Math.min(specified, cores) を使用
                    return specified;
                }
            }
            return Math.max(fallback, cores);
        } catch (Exception e) {
            return fallback;
        }
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
        List<SearchResult> cached = cache.get(src);
        if (cached != null) {
            return cached;
        }
        List<SearchResult> res = run(search_params.getMax_len(),
                search_params.getBeam_width(),
                search_params.getN_best(),
                src);
        cache.put(src, res);
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

        NDList enc = encode(src);

        List<Long> outputs = new ArrayList<>();
        outputs.add(aimodels.getVocab_tgt().getWord2Index().get(AiModels.BOS));

        BeamSearchNode root = new BeamSearchNode(null,
                outputs.get(outputs.size() - 1), 0, 0);
        List<BeamSearchNode> best_nodes = new ArrayList<>();
        best_nodes.add(root);

        long eosId = aimodels.getVocab_tgt().getWord2Index().get(AiModels.EOS);

        for (int loop = 0; loop < max_len; loop++) {
            boolean anyExpanded = false;
            List<BeamSearchNode> cand_nodes = new ArrayList<>();

            List<BeamSearchNode> expandable =
                    best_nodes.stream()
                            .filter(n -> n.decoder_input != eosId)
                            .collect(Collectors.toList());

            cand_nodes.addAll(
                    best_nodes.stream()
                            .filter(n -> n.decoder_input == eosId)
                            .collect(Collectors.toList())
            );

            if (expandable.isEmpty()) {
                break;
            }
            anyExpanded = true;

            if (parallelEnabled && expandable.size() > 1) {
                // 必要なら beam_width などに応じてタスク縮退（例: tasks = expandable.subList(0, Math.min(expandable.size(), parallelism));）
                List<Callable<List<BeamSearchNode>>> tasks = new ArrayList<>();
                for (BeamSearchNode node : expandable) {
                    tasks.add(() -> expandNode(node, enc, beam_width, eosId));
                }
                try {
                    List<Future<List<BeamSearchNode>>> futures = executor.invokeAll(tasks);
                    for (Future<List<BeamSearchNode>> f : futures) {
                        cand_nodes.addAll(f.get());
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Beam search interrupted", ie);
                } catch (ExecutionException ee) {
                    throw new RuntimeException("Parallel beam expansion failed", ee.getCause());
                }
            } else {
                for (BeamSearchNode node : expandable) {
                    cand_nodes.addAll(expandNode(node, enc, beam_width, eosId));
                }
            }

            List<BeamSearchNode> sorted =
                    cand_nodes.stream()
                            .sorted(Comparator.comparingDouble(BeamSearchNode::eval))
                            .collect(Collectors.toList());

            // 一般的ビームサーチ：ここでは beam_width に制限
            best_nodes = sorted.stream().limit(beam_width).collect(Collectors.toList());

            if (!anyExpanded) {
                break;
            }
        }

        best_nodes = best_nodes.stream()
                .sorted(Comparator.comparingDouble(BeamSearchNode::eval))
                .limit(n_best)
                .collect(Collectors.toList());

        for (BeamSearchNode n : best_nodes) {
            double prob = n.logProb;
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
                if (w.equals(AiModels.EOS) || w.equals(AiModels.BOS)) {
                    continue;
                }
                sb.append(w);
            }
            logger.debug("candidate={} prob={}", sb, prob);
            res.add(new SearchResult(sb.toString(), prob));
        }

        return res;
    }

    private List<BeamSearchNode> expandNode(BeamSearchNode node,
                                            NDList enc,
                                            int beam_width,
                                            long eosId) {

        if (node.decoder_input == eosId) {
            return Collections.singletonList(node);
        }

        List<Long> ys = concat_input(node);

        ai.djl.ndarray.NDManager localMgr = threadLocalMgr.get();
        ParameterStore ps = threadLocalPS.get();

        Shape outputShape = new Shape(ys.size(), 1);
        long[] outputLong = ys.stream().mapToLong(i -> i).toArray();
        NDArray outputTensor = localMgr.create(outputLong, outputShape);
        NDList outputTensorList = new NDList(outputTensor);

        NDList tgt_emb = aimodels.getTgt_tok_emb().getBlock().forward(ps, outputTensorList, false);
        NDList tgt_pos = aimodels.getPositional_encoding().getBlock().forward(ps, tgt_emb, false);
        NDList tgt_mask = generate_square_subsequent_mask(outputLong.length);

        NDList tgt = tgt_pos.addAll(enc).addAll(tgt_mask);

        NDList dec = aimodels.getDecoder().getBlock().forward(ps, tgt, false);
        NDArray out = dec.get(0).transpose(1, 0, 2).squeeze(0);
        long d = out.size(0);
        NDArray lastStep = out.get(d - 1).reshape(new Shape(1, out.size(1)));

        NDList prob = aimodels.getGenerator().getBlock().forward(ps, new NDList(lastStep), false);
        NDArray softmaxed = prob.get(0).softmax(1);

        NDList top = softmaxed.topK(beam_width, 1);
        NDArray nwords = top.get(1).transpose(1, 0);
        NDArray nprobs = top.get(0).transpose(1, 0);

        List<BeamSearchNode> newNodes = new ArrayList<>();
        for (int j = 0; j < nwords.size(); j++) {
            long nextWord = nwords.getLong(j);
            float nextProb = nprobs.getFloat(j);
            BeamSearchNode newNode = new BeamSearchNode(
                    node,
                    nextWord,
                    node.logProb + Math.log(nextProb),
                    node.length + 1
            );
            newNodes.add(newNode);
        }
        return newNodes;
    }

    @Override
    public void close() {
        super.close();
        if (executor != null) {
            executor.shutdown();
        }
        try {
            threadLocalMgr.remove();
            threadLocalPS.remove();
        } catch (Exception ignored) {}
    }
}



///**
// * 学習済みのモデルから，ビームサーチを行い，推論結果を返す
// */
//public final class BeamSearch extends AbstSearch {
//    private static final Logger logger = LogManager.getLogger(BeamSearch.class);
//    private final BeamCache cache;
//
//    /**
//     * コンストラクタ
//     * @param aimodels AiModel pythonで計算したモデルデータ
//     * @throws Exception 一般的なエラー
//     */
//    public BeamSearch(AiModels aimodels) throws Exception {
//        super(aimodels);
//        cache = BeamCache.newInstance();
//    }
//
//    /**
//     * ビームサーチを行う時の一つのノード情報
//     */
//    private static final class BeamSearchNode {
//        private final BeamSearchNode prevNode;
//        private final long decoder_input;
//        private final double logProb;
//        private final int length;
//        private final double eval;
//
//        private BeamSearchNode(BeamSearchNode prevNode, long decoder_input, double logProb, int length) {
//            this(prevNode, decoder_input, logProb, length, 0.6);
//        }
//
//        private BeamSearchNode(BeamSearchNode prevNode, long decoder_input, double logProb, int length, double alpha) {
//            this.prevNode = prevNode;
//            this.decoder_input = decoder_input;
//            this.logProb = logProb;
//            this.length = length;
//            this.eval = -this.logProb / Math.pow(((5 + this.length) / (5. + 1.)), alpha);
//        }
//
//        private double eval() {
//            return this.eval;
//        }
//    }
//
//    private List<Long> concat_input(BeamSearchNode n) {
//        List<Long> y = new ArrayList<>();
//        while (true) {
//            if (n == null) {
//                break;
//            }
//            y.add(n.decoder_input);
//            n = n.prevNode;
//        }
//        Collections.reverse(y);
//        return y;
//    }
//
//    @Override
//    public List<SearchResult> run(String src) {
//        List<SearchResult> res = cache.get(src);
//        if(res != null) {
//            return res;
//        }
//        res = run(search_params.getMax_len(), search_params.getBeam_width(), search_params.getN_best(), src);
//        cache.put(src, res);
//        return res;
//    }
//
//    /**
//     * ビームサーチを行い，推論結果を返す
//     * @param beam_width ビームサーチ幅，n_best以上にすること
//     * @param n_best 推論で作成する最大数
//     * @param src 入力する文字列，漢字を推論で出したいならばカナ，カナを推論で出したいならば漢字，コンストラクタのAiModelと整合させること
//     * @return 推論した結果のリスト
//     */
//    public List<SearchResult> run( int beam_width, int n_best, String src){
//        return run(search_params.getMax_len(), beam_width, n_best, src);
//    }
//
//    /**
//     * ビームサーチを行い，推論結果を返す
//     * @param max_len 推論で作成する文字列の最大長さ
//     * @param beam_width ビームサーチ幅，n_best以上にすること
//     * @param n_best 推論で作成する最大数
//     * @param src 入力する文字列，漢字を推論で出したいならばカナ，カナを推論で出したいならば漢字，コンストラクタのAiModelと整合させること
//     * @return 推論した結果のリスト
//     */
//    public List<SearchResult> run(int max_len, int beam_width, int n_best, String src){
//
//        List<SearchResult> res = new ArrayList<>();
//        logger.debug("src=" + src);
//
//        if (src.length() == 0) {
//            return res;
//        }
//        NDList enc = encode(src);
//        logger.debug("enc=" + enc);
//
//        ParameterStore ps = new ParameterStore();
//
//        List<Long> outputs = new ArrayList<>();
//        outputs.add(aimodels.getVocab_tgt().getWord2Index().get(AiModels.BOS));
//
//        BeamSearchNode node = new BeamSearchNode(null, outputs.get(outputs.size() - 1), 0, 0);
//        List<BeamSearchNode> best_nodes = new ArrayList<>();
//        best_nodes.add(node);
//
//        for (int loop = 0; loop < max_len; loop++) {
//            boolean updated = false;
//            List<BeamSearchNode> cand_nodes = new ArrayList<>();
//            for (BeamSearchNode n : best_nodes) {
//                if (n.decoder_input == aimodels.getVocab_tgt().getWord2Index().get(AiModels.EOS)) {
//                    cand_nodes.add(n);
//                } else {
//                    updated = true;
//                    List<Long> ys = concat_input(n);
//
//                    Shape outputShape = new Shape(ys.size(), 1);
//                    long[] outputLong = ys.stream().mapToLong(i -> i).toArray();
//                    NDArray outputTensor = manager.create(outputLong, outputShape);
//                    NDList outputTensorList = new NDList(outputTensor);
//                    NDList tgt_emb = aimodels.getTgt_tok_emb().getBlock().forward(ps, outputTensorList, false);
//                    NDList tgt_pos = aimodels.getPositional_encoding().getBlock().forward(ps, tgt_emb, false);
//                    NDList tgt_mask = generate_square_subsequent_mask(outputLong.length);
//
//                    // memoryは後ろに追加する
//                    NDList tgt = tgt_pos.addAll(enc);
//                    // maskも後ろに追加する
//                    tgt = tgt.addAll(tgt_mask);
//
//                    NDList dec = aimodels.getDecoder().getBlock().forward(ps, tgt, false);
//                    NDArray out = dec.get(0);
//                    out = out.transpose(1, 0, 2);
//                    out = out.squeeze(0);
//                    long d = out.size(0);
//                    NDArray out2 = out.get(d - 1);
//                    out2 = out2.reshape(new Shape(1, out2.size(0)));
//                    NDList prob = aimodels.getGenerator().getBlock().forward(ps, new NDList(out2), false);
//                    NDArray prob_softmaxed = prob.get(0).softmax(1);
//                    prob = new NDList(prob_softmaxed);
//
//                    NDList prob_top = prob.get(0).topK(beam_width, 1);
//                    NDArray nwords = prob_top.get(1).transpose(1, 0);
//                    NDArray nprobs = prob_top.get(0).transpose(1, 0);
//
//                    for (int j = 0; j < nwords.size(); j++) {
//                        long next_word = nwords.getLong(j);
//                        float next_prob = nprobs.getFloat(j);
//                        BeamSearchNode new_node = new BeamSearchNode(n, next_word, n.logProb + Math.log(next_prob), n.length + 1);
//                        cand_nodes.add(new_node);
//                    }
//                }
//            }
//            List<BeamSearchNode> sorted_cand_nodes = cand_nodes.stream().sorted(Comparator.comparingDouble(BeamSearchNode::eval)).collect(Collectors.toList());
//            best_nodes = sorted_cand_nodes.stream().limit(n_best).collect(Collectors.toList());
//            if (!updated) {
//                break;
//            }
//        }
//        for (BeamSearchNode n : best_nodes) {
//            double prob = n.logProb;
//            List<Long> token = new ArrayList<>();
//            while (true) {
//                token.add(n.decoder_input);
//                n = n.prevNode;
//                if (n == null) {
//                    break;
//                }
//            }
//            Collections.reverse(token);
//
//            StringBuilder sb = new StringBuilder();
//            for (Long t : token) {
//                String nw = aimodels.getVocab_tgt().getIndex2Word().get(t.longValue());
//                if (nw.equals(AiModels.EOS) || nw.equals(AiModels.BOS)) {
//                    continue;
//                }
//                sb.append(nw);
//            }
//            logger.debug("sb=" + sb + ",prob=" + prob);
//            res.add(new SearchResult(sb.toString(), prob));
//        }
//
//        return res;
//    }
//}
