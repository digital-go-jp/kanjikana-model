package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.executor.match.strategy.AbstStrategyEnsemble;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.model.ModelStatus;
import jp.go.digital.kanjikana.core.model.impl.IAiClassifyCharModel;
import jp.go.digital.kanjikana.core.model.impl.IAiClassifyWordModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;


/**
 * AI２値分類
 */
public class StrategyOnlyAiClassify extends AbstStrategyEnsemble {
    private static StrategyIF sb=null;
    private static final Logger logger = LogManager.getLogger(StrategyOnlyAiClassify.class);

    @Override
    public List<ModelIF> getModelList() throws Exception{
        return Arrays.asList(new IAiClassifyWordModel(),new IAiClassifyCharModel());
    }
    private StrategyOnlyAiClassify() throws Exception{
        setUnReliableModels(getModelList());  // AIモデル
    }

    /**
     * Ensembleモデルを返す
     * @return Ensembleモデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyOnlyAiClassify();
        }
        return sb;
    }
    @Override
    protected boolean isModelOk(ModelData modelData) {
        return modelData.getStatus() ==  ModelStatus.OK;
    }

    /**
     * モデルを順次用いてチェックする
     * @param modelData 前のモデルで判定した結果を入力する
     * @param kanji　漢字姓名　「山田　太郎」
     * @param kana　カタカナ姓名　「ヤマダ　タロウ」
     * @return チェックで漢字とカナが一致したかどうか
     * @throws Exception 一般的なエラー
     */
    @Override
    public boolean simpleModelCheck(ModelData modelData, String kanji, String kana) throws Exception{
        return false;
    }
}
