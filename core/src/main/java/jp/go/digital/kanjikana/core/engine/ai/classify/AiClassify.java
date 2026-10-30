package jp.go.digital.kanjikana.core.engine.ai.classify;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.Translator;
import jp.go.digital.kanjikana.core.engine.ai.AbstAiEngine;

import java.util.List;
import java.util.Map;

public class AiClassify extends AbstAiEngine {
    private final AbstClassifyEngine model;

    /**
     * ２値分類クラス
     * @param model trainで計算しtorch scriptへ返還したモデル及びパラメタ
     */
    public AiClassify(AbstClassifyEngine model){

        this.model = model;
    }

    /**
     * 漢字とカナを単語単位で入力して一致判定する
     * @param kanji 漢字姓名 「山田」
     * @param kana カナ姓名 「ヤマダ」
     * @return 判定結果
     * @throws Exception 一般的なエラー
     */
    public ClassifyResult run(String kanji, String kana) throws Exception{
        String str= kanji + "_" +kana;
        float prob =  predict(str);
        return new ClassifyResult(kanji,kana,prob);
    }
    private NDArray encode(NDManager manager, String name, Map<String, Long> char2id, int maxLen) {

        // 必須トークン検証（キー名は SpecialToken の定義に合わせる）
        Long bosId = char2id.get(BOS);
        Long eosId = char2id.get(EOS);
        Long sepId = char2id.get(SEP);
        Long unkId = char2id.get(UNK);

        if (bosId == null || eosId == null || sepId == null || unkId == null) {
            throw new IllegalStateException(
                    "char2id is missing required special tokens. " +
                            "BOS=" + BOS + ", EOS=" + EOS + ", SEP=" + SEP + ", UNK=" + UNK
            );
        }

        // PAD を辞書から取る（無ければ 0 を使うが、これは要整合性）
        Long padId = char2id.getOrDefault(PAD, 0L);

        // vocabSize 推定（辞書の最大ID+1）
        long vocabSize = char2id.values().stream().mapToLong(Long::longValue).max().orElse(0) + 1;

        long[] ids = new long[maxLen];
        // PADで埋める（0初期値に依存しない）
        for (int i = 0; i < maxLen; i++) ids[i] = padId;

        int idx = 0;
        ids[idx++] = bosId;

        for (int i = 0; i < name.length() && idx < maxLen - 1; i++) {
            String c = String.valueOf(name.charAt(i));
            long id;
            if ("_".equals(c)) {
                id = sepId;
            } else {
                id = char2id.getOrDefault(c, unkId);
            }

            // 範囲外検知（ここが今回の embedding 落ちの根本原因）
            if (id < 0 || id >= vocabSize) {
                throw new IllegalStateException(
                        "Token id out of range: char='" + c + "' id=" + id + " vocabSize=" + vocabSize
                );
            }

            ids[idx++] = id;
        }

        if (idx < maxLen) {
            ids[idx++] = eosId;
        }

        return manager.create(ids, new Shape(maxLen)).toType(DataType.INT64, false);
    }

    private float predict(String name) throws Exception {
        if (name.startsWith("_") || name.endsWith("_")) return 0;// kanji or kanaがブランクを考慮
        Device device = Device.cpu();
        Map<String, Long> char2id = this.model.getChar2id();
        Model classifier = this.model.getClassifier();

        try (NDManager manager = NDManager.newBaseManager(device)) {
            int max_len = Integer.parseInt(model.getParams().get("max_len")); // TODO max_len must be defined in propfile
            NDArray x1d = encode(manager, name, char2id,max_len);   // (L,)
            NDArray x = x1d.expandDims(0);                          // (1, L)

            // Translator
            Translator<NDList, NDList> translator = new NoopTranslator();

            try (Predictor<NDList, NDList> predictor = classifier.newPredictor(translator)) {
                NDList out = predictor.predict(new NDList(x));

                // out[0] = logits shape: (1,2) を想定
                NDArray logits = out.singletonOrThrow();           // (1,2)
                NDArray probs = logits.softmax(1);                 // dim=1

                //float pFalse = probs.getFloat(0, 0);
                float pTrue  = probs.getFloat(0, 1);
                return pTrue;
            }
        }
    }
}


