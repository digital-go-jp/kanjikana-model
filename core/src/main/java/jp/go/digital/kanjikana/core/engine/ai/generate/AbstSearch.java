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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推論を行う抽象クラス
 */
abstract class AbstSearch implements Search {
    private static final Logger logger = LogManager.getLogger(AbstSearch.class);

    protected final GenerateEngine aimodels;
    protected final NDManager manager;

    protected final SearchParam search_params = new SearchParam();

    /** 系列長ごとの subsequent mask を使い回す（ステップ毎の生成を避ける） */
    private final Map<Integer, NDArray> maskCache = new HashMap<>();

    /**
     * コンストラクタ
     * @param aimodels AiModel pythonで計算したモデルデータ
     * @throws Exception 一般的なエラー
     */
    public AbstSearch(GenerateEngine aimodels) throws Exception {
        this.aimodels = aimodels;
        Engine engine = Engine.getEngine(Engine.getDefaultEngineName());
        JniUtils.setGraphExecutorOptimize(false);
        this.manager = engine.newBaseManager(Device.cpu());
    }

    /**
     * モデルデータを閉じる。Djlライブラリでデータを保持しているので，終了時にこれを呼ばないとメモリリークする
     */
    @Override
    public void close(){
        //this.aimodels.close();
        this.maskCache.clear();
        this.manager.close();
    }

    static List<Long> toIndex(List<String> src_list, Map<String, Long> map) {
        List<Long> indexes = new ArrayList<>();
        indexes.add(map.get(GenerateEngine.BOS));
        for (String item : src_list) {
            if (map.containsKey(item)) {
                indexes.add(map.get(item));
            } else {
                indexes.add(map.get(GenerateEngine.UNK));
            }
        }
        indexes.add(map.get(GenerateEngine.EOS));
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
        ParameterStore ps = new ParameterStore();

        try (NDManager scope = manager.newSubManager()) {
            NDArray inputTensor = scope.create(inputLong, inputShape);
            NDList inputTensorList = new NDList(inputTensor);

            NDList src_emb = aimodels.getSrc_tok_emb().getBlock().forward(ps, inputTensorList, false);
            NDList src_pos = aimodels.getPositional_encoding().getBlock().forward(ps, src_emb, false);
            NDList enc = aimodels.getEncoder().getBlock().forward(ps, src_pos, false);
            NDArray memory = enc.get(0);
            memory.attach(manager); // 中間テンソルだけを scope で解放する
            return new NDList(memory);
        }
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
        NDArray cached = maskCache.get(siz);
        if (cached == null) {
            boolean[][] mask = triangular_matrix(siz);
            cached = manager.create(mask).transpose();
            maskCache.put(siz, cached);
        }
        return new NDList(cached);
    }
}
