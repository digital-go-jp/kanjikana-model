package jp.go.digital.kanjikana.core.executor.match.strategy;

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class AbstStrategyEnsemble extends AbstStrategy{

    private static final Logger logger = LogManager.getLogger(AbstStrategyEnsemble.class);
    protected int okcnt=0;


    @Override
    public boolean modelCheck(ModelData modelData, String kanji, String kana) throws Exception {
        if(simpleModelCheck(modelData, kanji, kana)){ // 簡易モデルでチェック。NGならばアンサンブルへ
            return true;
        }
        //List<Double> probList = new ArrayList<>();
        int idx=0;
        this.okcnt=0;

        for(List<ModelIF> unreliable: UnReliableModels){
            Date stdate=new Date();
            logger.debug(idx+",start,"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(stdate));
            //ModelData md = SerializationUtils.clone(modelData); // reliableなモデルで計算したものを保存しておく
            ModelData md = modelData.copy();
            for(ModelIF model: unreliable){
                md = model.run(kanji,kana, md);
                logger.debug(model.getClass()+",kanji="+kanji+",isOK="+md.isOk());
                if(md.isOk()){
                    break;
                }
            }
            if(!md.isOk()){ // 前の結果を引きずる形で実施した際にダメだった際には，リセットする　「大　阪府」　この場合，「大」「おお」が辞書にあるが，「阪府」「さかふ」は辞書にないので，一家リセットして「大　阪府」でWordモデルで，次にCharモデル「大阪府」でできるようにする
                //md = SerializationUtils.clone(modelData); // reliableなモデルで計算したものを保存しておく
                md = modelData.copy();
                md.resetTopResult();
                for(ModelIF model: unreliable){
                    md = model.run(kanji,kana, md);
                    logger.debug(model.getClass()+",kanji="+kanji+",isOK="+md.isOk());
                    if(md.isOk()){
                        break;
                    }
                }
            }
            //probList.add(md.getModelProbability());
            if (md.isOk()) {
                okcnt+=1;  // モデルがOKを出した数を数えておく
            }

            modelData.addEnsembleResult(md);
            Date eddate = new Date();
            logger.debug(idx+",end,"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(eddate));
            logger.debug(idx+",time,"+(eddate.getTime()-stdate.getTime()));
            idx = idx+1;
        }

        return isModelOk(modelData);

    }

    protected abstract boolean isModelOk(ModelData modelData);
}
