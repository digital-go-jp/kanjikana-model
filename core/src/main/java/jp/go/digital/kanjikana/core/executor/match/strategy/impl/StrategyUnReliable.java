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
 * 詳細モデルの信頼度低モデルのアンサンブル
 * Ensembleモデルをシングルトンで定義する。
 * このEnsembleでは，あまり信頼度の高くないモデルを組み合わせて実行し，その結果を多数決で判定する
 */
public final class StrategyUnReliable extends AbstStrategyEnsemble {
    private static StrategyIF sb=null;

    private static final Logger logger = LogManager.getLogger(StrategyUnReliable.class);
    @Override
    public List<ModelIF> getModelList() throws Exception {
        return List.of();
    }

    private StrategyUnReliable() throws Exception{
        super();

        // 辞書モデル
        setUnReliableModels(StrategyOnlyDict.newInstance().getModelList());
        // 統計モデル
        setUnReliableModels(StrategyOnlyStatistics.newInstance().getModelList());
        // AIモデル
        setUnReliableModels(StrategyOnlyAiGenerate.newInstance().getModelList());
    }


    /**
     * Ensembleモデルを返す
     * @return Ensembleモデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyUnReliable();
        }
        return sb;
    }

    @Override
    protected boolean isModelOk(ModelData modelData) {
        // 複数のモデルでOKが出たモデルの数を数えておき，閾値以上の正解数ならばOKを返す
        if(super.okcnt/(double)getUnReliableModels().size()>=getEnsembleOkUnderLimit()){
            modelData.setStatus(ModelStatus.OK);
            return true;
        }
        return false;
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
