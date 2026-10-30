package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AiClassifyCharEngine;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.model.AbstCharModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AiClassifyCharModel extends AbstCharModel {
    private static final Logger logger = LogManager.getLogger(AiClassifyCharModel.class);

    public AiClassifyCharModel() throws Exception {
        super(new AiClassifyCharEngine(true));
    }
    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(kanji_item, kana_item, modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData.setModel(this);

        // 全部やり直す
        //modelData.setTopResult(new ResultEngineParts(kanji_item, kana_item));
        //modelData.resetTopResult();

        ModelData md = exec(kanji_item, kana_item, modelData);
        md.setModel(this);
        return md;
    }

    @Override
    protected ResultEngineParts run_sub(String kanji_str, String kana_str) throws Exception{

        // スペースなしで検査
        return super.run_sub(kanji_str.replace("　", ""), kana_str.replace("　", ""))
                ;
    }

    @Override
    public boolean isAiModel(){
        return true;
    }
}
