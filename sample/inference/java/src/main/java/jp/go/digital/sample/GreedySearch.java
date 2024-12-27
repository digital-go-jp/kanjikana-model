package jp.go.digital.sample;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.ParameterStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 学習済みのモデルから，貪欲法を行い，推論結果を返す
 */
public final class GreedySearch extends AbstSearch {

    public GreedySearch(AiModels aimodels, SearchParam search_param) throws Exception{
        super(aimodels,search_param);
    }

    @Override
    public List<SearchResult> run(String src) {
        List<SearchResult> res = new ArrayList<>();

        if(src.length()==0){
            return res;
        }
        NDList enc = encode(src);

        ///  above OK
        ParameterStore ps = new ParameterStore();

        double sum_prob=0;
        List<Long> outputs = new ArrayList<>();
        outputs.add(aimodels.getVocab_tgt().getWord2Index().get(AiModels.BOS));
        for(int loop=0; loop< search_params.getMax_len(); loop++) {

            Shape outputShape = new Shape(outputs.size(), 1);
            long[] outputLong = outputs.stream().mapToLong(i -> i).toArray();
            NDArray outputTensor = manager.create(outputLong, outputShape);
            NDList outputTensorList = new NDList(outputTensor);

            NDList tgt_emb = aimodels.getTgt_tok_emb().getBlock().forward(ps, outputTensorList, false);
            NDList tgt_pos = aimodels.getPositional_encoding().getBlock().forward(ps, tgt_emb, false);

            NDList tgt_mask = generate_square_subsequent_mask(outputLong.length);

            // memoryは後ろに追加する
            NDList tgt = tgt_pos.addAll(enc);
            tgt = tgt.addAll(tgt_mask);

            NDList dec = aimodels.getDecoder().getBlock().forward(ps, tgt, false);
            NDArray out = dec.get(0);
            out = out.transpose(1, 0, 2);
            out = out.squeeze(0);
            long d = out.size(0);
            var out2 = out.get(d-1);
            out2 = out2.reshape(new Shape(1,out2.size(0)));
            NDList prob = aimodels.getGenerator().getBlock().forward(ps, new NDList(out2), false);
            NDArray prob_softmaxed = prob.get(0).softmax(1);
            prob = new NDList(prob_softmaxed);

            NDList prob_top = prob.get(0).topK(1, 1);
            long next_word = prob_top.get(1).getLong(0);
            float next_prob = prob_top.get(0).getFloat(0);

            sum_prob+=Math.log(next_prob);

            outputs.add(next_word);
            if(next_word==aimodels.getVocab_tgt().getWord2Index().get(AiModels.EOS)){
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        for(long w : outputs){
            String nw = aimodels.getVocab_tgt().getIndex2Word().get(w);
            if(nw.equals(AiModels.BOS) || nw.equals(AiModels.EOS)){
                continue;
            }
            sb.append(nw);
        }
        res.add(new SearchResult(sb.toString(),sum_prob));

        return res;
    }
}
