package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.engine.NandokuEngine;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;
import jp.go.digital.kanjikana.core.model.AbstCharModel;
import jp.go.digital.kanjikana.core.model.ModelData;

import java.util.Arrays;

/**
 * 法務省の難読名許容の指針
 * 置き字、漢字の読みの一部、熟字訓を許容する
 * 文字単位で実施 「渡辺 雲母」「わたなべ きらら」 渡辺雲母ーわたなべきらら でチェック
 * 漢字名に異体字があれば置き換える
 * @version 1.8 DictをNormalizedから通常にした。禁則処理の対応のため
 * @since 1.7
 */
class NandokuCharModel extends AbstCharModel {
    NandokuCharModel() throws Exception{
        super(new NandokuEngine(Arrays.asList( DictAsIs.newInstance(),   DictTankanji.newInstance()),false));
    }

    public NandokuCharModel(EngineIF engine) throws Exception{
        super(engine);
    }

    private String trim(String s){
        return s.replace(" ","").replace("　","");
    }

    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(trim(kanji_item), trim(kana_item), modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData = exec(trim(kanji_item), trim(kana_item), modelData);
        modelData.setModel(this.getClass());
        setStatus(modelData);
        modelData.setModel(this.getClass());
        return modelData;
    }

    @Override
    protected ResultEngineParts run_sub(String kanji_parts, String kana_parts) throws Exception{
        ResultEngineParts topResult = getEngine().check(trim(kanji_parts),trim(kana_parts));
        return topResult;
    }
}
