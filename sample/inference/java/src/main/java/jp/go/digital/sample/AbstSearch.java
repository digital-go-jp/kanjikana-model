package jp.go.digital.sample;

import ai.djl.Device;
import ai.djl.engine.Engine;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.pytorch.jni.JniUtils;
import ai.djl.training.ParameterStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推論を行う抽象クラス
 */
abstract class AbstSearch implements Search{
    private static final Logger logger = LogManager.getLogger(AbstSearch.class);

    protected final AiModels aimodels;
    protected final NDManager manager;

    protected final SearchParam search_params;

    /**
     * コンストラクタ
     * @param aimodels AiModel pythonで計算したモデルデータ
     * @throws Exception 一般的なエラー
     */
    public AbstSearch(AiModels aimodels, SearchParam search_param) throws Exception {
        this.aimodels = aimodels;
        this.search_params = search_param;
        Engine engine = Engine.getEngine(Engine.getDefaultEngineName());
        JniUtils.setGraphExecutorOptimize(false);
        this.manager = engine.newBaseManager(Device.cpu());
    }

    /**
     * モデルデータを閉じる。Djlライブラリでデータを保持しているので，終了時にこれを呼ばないとメモリリークする
     */
    @Override
    public void close(){
        this.manager.close();
    }

    static List<Long> toIndex(List<String> src_list, Map<String, Long> map) {
        List<Long> indexes = new ArrayList<>();
        indexes.add(map.get(AiModels.BOS));
        for (String item : src_list) {
            if (map.containsKey(item)) {
                indexes.add(map.get(item));
            } else {
                indexes.add(map.get(AiModels.UNK));
            }
        }
        indexes.add(map.get(AiModels.EOS));
        return indexes;
    }

    protected NDList encode(String src) {
        List<String> src_list = new ArrayList<>();
        for (int i = 0; i < src.length(); i++) {
            src_list.add(src.substring(i, i + 1));
        }

        List<Long> inputs = AbstSearch.toIndex(src_list, aimodels.getVocab_src().getWord2Index());
        Shape inputShape = new Shape(inputs.size(), 1);
        long[] inputLong = inputs.stream().mapToLong(i -> i).toArray();
        NDArray inputTensor = manager.create(inputLong, inputShape);
        NDList inputTensorList = new NDList(inputTensor);
        ParameterStore ps = new ParameterStore();

        NDList src_emb = aimodels.getSrc_tok_emb().getBlock().forward(ps, inputTensorList, false);
        NDList src_pos = aimodels.getPositional_encoding().getBlock().forward(ps, src_emb, false);
        NDList enc = aimodels.getEncoder().getBlock().forward(ps, src_pos, false);
        return enc;
    }

    protected boolean[][] triangular_matrix(int siz) {
        boolean[][] mask = new boolean[siz][siz];
        int row = mask.length;
        int col = mask[0].length;
        // looping over the whole matrix
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                mask[i][j] = false;
                if (i > j) {
                    mask[i][j] = true;
                }
            }
        }
        return mask;
    }

    protected NDList generate_square_subsequent_mask(int siz) {
        boolean[][] mask = triangular_matrix(siz);
        NDArray maskTensor = manager.create(mask);
        maskTensor = maskTensor.transpose();
        return new NDList(maskTensor);
    }

}
