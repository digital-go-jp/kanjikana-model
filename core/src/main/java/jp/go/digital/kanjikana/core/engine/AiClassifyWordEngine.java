package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.engine.ai.classify.AiClassify;
import jp.go.digital.kanjikana.core.engine.ai.classify.AbstClassifyEngine;
import jp.go.digital.kanjikana.core.engine.ai.classify.ClassifyEngEngine;
import jp.go.digital.kanjikana.core.engine.ai.classify.ClassifyJpnEngine;
import jp.go.digital.kanjikana.core.engine.ai.classify.ClassifyResult;

import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaiji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaijiDummy;
import jp.go.digital.kanjikana.core.utils.FJClassify;
import jp.go.digital.kanjikana.core.utils.Moji;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * AIの２値分類を用いて単語単位で突合するエンジン
 */
public class AiClassifyWordEngine extends  AbstEngine{
    private static final Logger logger = LogManager.getLogger(AiClassifyWordEngine.class);

    private final DictIF idic; // 異体字

    public AiClassifyWordEngine(boolean hasItaiji) throws Exception{
        if(hasItaiji) {
            this.idic = DictItaiji.newInstance();
        }else{
            this.idic = DictItaijiDummy.newInstance();
        }
    }

    private boolean checkMoji(String a, String b){
        return Moji.normalize(a).equals(Moji.normalize(b));
    }

    @Override
    public boolean isValidEngine(){
        return true;
    }


    private AbstClassifyEngine getEngine(String kanji) throws Exception{
        if (FJClassify.isForeigner(kanji)){
            return ClassifyEngEngine.newInstance();
        }else{
            return ClassifyJpnEngine.newInstance();
        }
    }

    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part_orig 田中　太郎
     * @param kana_part タナカ　タロウ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part_orig, String kana_part){
        AiClassify classify =null;
        try {
            String kanji_part = replace_itaiji(kanji_part_orig,idic);

            AbstClassifyEngine cm = getEngine(kanji_part);
            classify = new AiClassify(cm);
            ClassifyResult res = classify.run( kanji_part,kana_part);

            ResultEngineParts.Type type= ResultEngineParts.Type.NOT_FOUND;
            if(res.isOk()){
                type=ResultEngineParts.Type.OK;
            }
            return new ResultEngineParts(type,kanji_part, kana_part, new ResultAttr(res.getProbability(),new ArrayList<>(),"Probability:"+res.getProbability()),this,null);
        }catch (Exception e){
            e.fillInStackTrace();
            logger.fatal(e);
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part_orig,kana_part,new ResultAttr(), this, null);
    }
}
