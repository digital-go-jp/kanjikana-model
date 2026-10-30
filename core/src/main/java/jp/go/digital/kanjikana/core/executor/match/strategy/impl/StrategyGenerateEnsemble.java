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
 * 詳細モデル
 * Ensembleモデルをシングルトンで定義する。
 * このEnsembleでは，あまり信頼度の高くないモデルを組み合わせて実行し，その結果を多数決で判定する
 */
public final class StrategyGenerateEnsemble extends AbstStrategyEnsemble {
    private static StrategyIF sb=null;
    private static final Logger logger = LogManager.getLogger(StrategyGenerateEnsemble.class);
    @Override
    public List<ModelIF> getModelList() throws Exception {
        return List.of(); // dummy
    }
    private StrategyGenerateEnsemble() throws Exception{
        super();
        setReliableModels(StrategyBasic.newInstance().getModelList());

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
            sb = new StrategyGenerateEnsemble();
        }
        return sb;
    }


    @Override
    protected boolean isModelOk(ModelData modelData) {
        // 複数のモデルでOKが出たモデルの数を数えておき，閾値以上の正解数ならばOKを返す
        int siz=getUnReliableModels().size();
        if(super.okcnt/(double)siz>=getEnsembleOkUnderLimit()){
            modelData.setStatus(ModelStatus.OK);
            return true;
        }
        return false;
    }
}
