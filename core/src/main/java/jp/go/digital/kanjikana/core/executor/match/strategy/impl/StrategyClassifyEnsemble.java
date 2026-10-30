package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.executor.match.strategy.AbstStrategyEnsemble;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.model.ModelStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * 詳細モデル Classifyを追加
 * Ensembleモデルをシングルトンで定義する。
 * このEnsembleでは，あまり信頼度の高くないモデルを組み合わせて実行し，その結果を多数決で判定する
 */
public class StrategyClassifyEnsemble extends AbstStrategyEnsemble {
    private static StrategyIF sb=null;

    private static final Logger logger = LogManager.getLogger(StrategyClassifyEnsemble.class);
    @Override
    public List<ModelIF> getModelList() throws Exception {
        return List.of(); // dummy
    }
    private StrategyClassifyEnsemble() throws Exception{
        super();
        setReliableModels(StrategyBasic.newInstance().getModelList());

        // 辞書モデル
        setUnReliableModels(StrategyOnlyDict.newInstance().getModelList());
        // 統計モデル
        setUnReliableModels(StrategyOnlyStatistics.newInstance().getModelList());
        // AIモデル
        //setUnReliableModels(StrategyOnlyAi.newInstance().getModelList());

        // AI２値分類モデル
        setUnReliableModels(StrategyOnlyAiClassify.newInstance().getModelList());
    }


    /**
     * Ensembleモデルを返す
     * @return Ensembleモデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyClassifyEnsemble();
        }
        return sb;
    }

    @Override
    protected boolean isModelOk(ModelData modelData) {
        return modelData.getStatus() ==  ModelStatus.OK;
    }
}
