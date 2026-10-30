package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AbstEngine;
import jp.go.digital.kanjikana.core.engine.AiClassifyWordEngine;
import jp.go.digital.kanjikana.core.engine.ResultAttr;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.model.AbstWordModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AiClassifyWordModel extends AbstWordModel {
    private static final Logger logger = LogManager.getLogger(AiClassifyWordModel.class);

    public AiClassifyWordModel() throws Exception{
        super(new AiClassifyWordEngine( true));
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
        ModelData md = exec(kanji_item, kana_item, modelData);
        md.setModel(this);
        return md;
    }

    @Override
    protected ResultEngineParts run_sub(String kanji_str, String kana_str) throws Exception{
        ResultEngineParts topResult = null; // WordEngine
        String[] kanji_parts = kanji_str.split("　");
        String[] kana_parts = kana_str.split("　");
        if(kanji_parts.length==kana_parts.length) {
            ResultEngineParts prevResult = null;
            ResultEngineParts nowResult = null;
            for (int i = 0; i < kanji_parts.length; i++) {
                nowResult = getEngine().check(kanji_parts[i], kana_parts[i]);

                if (prevResult != null) {
                    prevResult.setNextResult(nowResult);
                    nowResult.setPrevResult(prevResult);
                }
                prevResult = nowResult;
                if (topResult == null) {
                    topResult = prevResult;
                }
            }
        }
        if(topResult!=null){
            logger.debug("ClassifyWordModel.OK");
            return topResult;
        }else{
            logger.debug("ClassifyWordModel.NG");
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_str,kana_str,new ResultAttr(), (AbstEngine)this.getEngine(),null);
        }

    }

    @Override
    public boolean isAiModel(){
        return true;
    }
}