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

package jp.go.digital.kanjikana.core.executor.match.strategy;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.model.AbstModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * impl以下で定義するStrategyで共通で使用するFunctionをここで定義しておく
 */
public abstract class AbstStrategy implements StrategyIF{
    private static final Logger logger = LogManager.getLogger(AbstStrategy.class);
    protected final List<ModelIF> models = new ArrayList<>(); ;

    protected static final double EnsembleOkUnderLimit= Double.parseDouble(Resources.getProperty(Resources.PropKey.STRATEGY_EnsembleOkUnderLimit)); // アンサンブルでOKを出したモデルの割合の最小値
    protected final List<List<ModelIF>> UnReliableModels = new ArrayList<>(); //

    protected void setUnReliableModels(List<ModelIF> models){
        List<ModelIF> validModels=new ArrayList<>();
        for(ModelIF model:models){
            if(model.isValidModel()){
                validModels.add(model);
            }
        }
        if(!validModels.isEmpty()){
            this.UnReliableModels.add(validModels);
        }
    }

    protected void setReliableModels(List<ModelIF> models){
        for(ModelIF model:models){
            if(model.isValidModel()){
                this.models.add(model);
            }
        }
    }


    /**
     * あまり信頼度が高くないモデルのリスト
     * @return
     */
    protected List<List<ModelIF>> getUnReliableModels(){
        return this.UnReliableModels;
    }

    /**
     * アンサンブルでOKを出したモデルの割合の最小値
     * @return
     */
    protected double getEnsembleOkUnderLimit(){
        return EnsembleOkUnderLimit;
    }

    /**
     * アンサンブルで計算したそれぞれの確率を基に、このストラテジでの結果を判定する
     * @param probs モデルごとの確率
     * @return 漢字とカナの一致の判定結果
     */
    /*
    protected boolean isModelOk(List<Double> probs){
        double avg = probs.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return avg >= EnsembleOkUnderLimit;
    }*/

    /**
     * モデルを順次用いてチェックする
     * @param modelData 前のモデルで判定した結果を入力する
     * @param kanji　漢字姓名　「山田　太郎」
     * @param kana　カタカナ姓名　「ヤマダ　タロウ」
     * @return チェックで漢字とカナが一致したかどうか
     * @throws Exception 一般的なエラー
     */
    public abstract boolean modelCheck(ModelData modelData, String kanji, String kana) throws Exception;

    /**
     * モデルを順次用いてチェックする
     * @param modelData 前のモデルで判定した結果を入力する
     * @param kanji　漢字姓名　「山田　太郎」
     * @param kana　カタカナ姓名　「ヤマダ　タロウ」
     * @return チェックで漢字とカナが一致したかどうか
     * @throws Exception 一般的なエラー
     */
    public boolean simpleModelCheck(ModelData modelData, String kanji, String kana) throws Exception{
        //modelData.setTopResult(new ResultEngineParts("","")); // reset
        modelData.resetTopResult();
        for (ModelIF model : models) {
            modelData = model.run(kanji,kana, modelData);

            logger.debug(model.getClass()+",kanji="+kanji+",isOK="+modelData.isOk());

            if (modelData.isOk()) {
                modelData.setModel((AbstModel)model);
                return true;
            }
        }
        if (modelData.isOk()) {
            return true;
        }
        return false;
    }

}
