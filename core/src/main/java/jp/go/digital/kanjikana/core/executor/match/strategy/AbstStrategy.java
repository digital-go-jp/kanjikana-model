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

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
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

    private static final double EnsembleOkUnderLimit=0.5; // アンサンブルでOKを出したモデルの割合の最小値
    private final List<List<ModelIF>> UnReliableModels = new ArrayList<>(); //

    //protected AbstStrategy(List<ModelIF> models){
    //    this.models = models;
    //}

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

}
