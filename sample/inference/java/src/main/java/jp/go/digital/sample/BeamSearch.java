package jp.go.digital.sample;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.ParameterStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学習済みのモデルから，ビームサーチを行い，推論結果を返す
 */
public final class BeamSearch extends AbstSearch {
    private static final Logger logger = LogManager.getLogger(BeamSearch.class);

    /**
     * コンストラクタ
     * @param aimodels AiModel pythonで計算したモデルデータ
     * @throws Exception 一般的なエラー
     */
    public BeamSearch(AiModels aimodels, SearchParam search_param) throws Exception {
        super(aimodels,search_param);
    }

    /**
     * ビームサーチを行う時の一つのノード情報
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
        while (true) {
            if (n == null) {
                break;
            }
            y.add(n.decoder_input);
            n = n.prevNode;
        }
        Collections.reverse(y);
        return y;
    }

    @Override
    public List<SearchResult> run(String src) {
        return run(search_params.getMax_len(), search_params.getBeam_width(), search_params.getN_best(), src);
    }

    /**
     * ビームサーチを行い，推論結果を返す
     * @param beam_width ビームサーチ幅，n_best以上にすること
     * @param n_best 推論で作成する最大数
     * @param src 入力する文字列，漢字を推論で出したいならばカナ，カナを推論で出したいならば漢字，コンストラクタのAiModelと整合させること
     * @return 推論した結果のリスト
     */
    public List<SearchResult> run( int beam_width, int n_best, String src){
        return run(search_params.getMax_len(), beam_width, n_best, src);
    }

    /**
     * ビームサーチを行い，推論結果を返す
     * @param max_len 推論で作成する文字列の最大長さ
     * @param beam_width ビームサーチ幅，n_best以上にすること
     * @param n_best 推論で作成する最大数
     * @param src 入力する文字列，漢字を推論で出したいならばカナ，カナを推論で出したいならば漢字，コンストラクタのAiModelと整合させること
     * @return 推論した結果のリスト
     */
    public List<SearchResult> run(int max_len, int beam_width, int n_best, String src){

        List<SearchResult> res = new ArrayList<>();
        logger.debug("src=" + src);

        if (src.length() == 0) {
            return res;
        }
        NDList enc = encode(src);
        logger.debug("enc=" + enc);

        ParameterStore ps = new ParameterStore();

        List<Long> outputs = new ArrayList<>();
        outputs.add(aimodels.getVocab_tgt().getWord2Index().get(AiModels.BOS));

        BeamSearchNode node = new BeamSearchNode(null, outputs.get(outputs.size() - 1), 0, 0);
        List<BeamSearchNode> best_nodes = new ArrayList<>();
        best_nodes.add(node);

        for (int loop = 0; loop < max_len; loop++) {
            boolean updated = false;
            List<BeamSearchNode> cand_nodes = new ArrayList<>();
            for (BeamSearchNode n : best_nodes) {
                if (n.decoder_input == aimodels.getVocab_tgt().getWord2Index().get(AiModels.EOS)) {
                    cand_nodes.add(n);
                } else {
                    updated = true;
                    List<Long> ys = concat_input(n);

                    Shape outputShape = new Shape(ys.size(), 1);
                    long[] outputLong = ys.stream().mapToLong(i -> i).toArray();
                    NDArray outputTensor = manager.create(outputLong, outputShape);
                    NDList outputTensorList = new NDList(outputTensor);
                    NDList tgt_emb = aimodels.getTgt_tok_emb().getBlock().forward(ps, outputTensorList, false);
                    NDList tgt_pos = aimodels.getPositional_encoding().getBlock().forward(ps, tgt_emb, false);
                    NDList tgt_mask = generate_square_subsequent_mask(outputLong.length);

                    // memoryは後ろに追加する
                    NDList tgt = tgt_pos.addAll(enc);
                    // maskも後ろに追加する
                    tgt = tgt.addAll(tgt_mask);

                    NDList dec = aimodels.getDecoder().getBlock().forward(ps, tgt, false);
                    NDArray out = dec.get(0);
                    out = out.transpose(1, 0, 2);
                    out = out.squeeze(0);
                    long d = out.size(0);
                    NDArray out2 = out.get(d - 1);
                    out2 = out2.reshape(new Shape(1, out2.size(0)));
                    NDList prob = aimodels.getGenerator().getBlock().forward(ps, new NDList(out2), false);
                    NDArray prob_softmaxed = prob.get(0).softmax(1);
                    prob = new NDList(prob_softmaxed);

                    NDList prob_top = prob.get(0).topK(beam_width, 1);
                    NDArray nwords = prob_top.get(1).transpose(1, 0);
                    NDArray nprobs = prob_top.get(0).transpose(1, 0);

                    for (int j = 0; j < nwords.size(); j++) {
                        long next_word = nwords.getLong(j);
                        float next_prob = nprobs.getFloat(j);
                        BeamSearchNode new_node = new BeamSearchNode(n, next_word, n.logProb + Math.log(next_prob), n.length + 1);
                        cand_nodes.add(new_node);
                    }
                }
            }
            List<BeamSearchNode> sorted_cand_nodes = cand_nodes.stream().sorted(Comparator.comparingDouble(BeamSearchNode::eval)).collect(Collectors.toList());
            best_nodes = sorted_cand_nodes.stream().limit(n_best).collect(Collectors.toList());
            if (!updated) {
                break;
            }
        }
        for (BeamSearchNode n : best_nodes) {
            double prob = n.logProb;
            List<Long> token = new ArrayList<>();
            while (true) {
                token.add(n.decoder_input);
                n = n.prevNode;
                if (n == null) {
                    break;
                }
            }
            Collections.reverse(token);

            StringBuilder sb = new StringBuilder();
            for (Long t : token) {
                String nw = aimodels.getVocab_tgt().getIndex2Word().get(t.longValue());
                if (nw.equals(AiModels.EOS) || nw.equals(AiModels.BOS)) {
                    continue;
                }
                sb.append(nw);
            }
            logger.debug("sb=" + sb + ",prob=" + prob);
            res.add(new SearchResult(sb.toString(), prob));
        }

        return res;
    }
}
