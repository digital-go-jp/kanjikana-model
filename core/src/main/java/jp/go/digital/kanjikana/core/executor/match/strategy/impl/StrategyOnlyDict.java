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

import jp.go.digital.kanjikana.core.executor.match.strategy.AbstStrategy;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.model.ModelStatus;
import jp.go.digital.kanjikana.core.model.impl.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 詳細モデル
 * Ensembleモデルをシングルトンで定義する。
 * このEnsembleでは，あまり信頼度の高くないモデルを組み合わせて実行し，その結果を多数決で判定する
 */
public final class StrategyOnlyDict extends AbstStrategy {
    private static StrategyIF sb=null;
    private static final double EnsembleOkUnderLimit=0.5; // アンサンブルでOKを出したモデルの割合の最小値

    private final List<List<ModelIF>> UnReliableModels = new ArrayList<>(); //
    private final Logger logger = LogManager.getLogger(StrategyEnsemble.class);

    private StrategyOnlyDict() throws Exception{

        UnReliableModels.add(Arrays.asList(new IReliableWordModel(), new FWordModel(),new INandokuWordModel(), new IDictWordModel(), new FCharModel(), new IDictCharModel())); // 辞書モデル

    }

    /**
     * Ensembleモデルを返す
     * @return Ensembleモデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyOnlyDict();
        }
        return sb;
    }

    @Override
    public boolean modelCheck(ModelData modelData, String kanji, String kana) throws Exception {
        //if(super.modelCheck(modelData, kanji, kana)){ // 簡易モデルでチェック。NGならばアンサンブルへ
         //   return true;
        //}
        int idx=0;
        double okcnt=0.0; // モデルがOKを出した数
        for(List<ModelIF> unreliable: UnReliableModels){
            Date stdate=new Date();
            logger.debug(idx+",start,"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(stdate));
            ModelData md = SerializationUtils.clone(modelData); // reliableなモデルで計算したものを保存しておく
            for(ModelIF model: unreliable){
                md = model.run(kanji,kana, md);
                logger.debug(model.getClass()+",kanji="+kanji+",isOK="+md.isOk());
                if(md.isOk()){
                    break;
                }
            }
            if(!md.isOk()){ // 前の結果を引きずる形で実施した際にダメだった際には，リセットする　「大　阪府」　この場合，「大」「おお」が辞書にあるが，「阪府」「さかふ」は辞書にないので，一家リセットして「大　阪府」でWordモデルで，次にCharモデル「大阪府」でできるようにする
                md = SerializationUtils.clone(modelData); // reliableなモデルで計算したものを保存しておく
                md.resetTopResult();
                for(ModelIF model: unreliable){
                    md = model.run(kanji,kana, md);
                    logger.debug(model.getClass()+",kanji="+kanji+",isOK="+md.isOk());
                    if(md.isOk()){
                        break;
                    }
                }
            }

            if (md.isOk()) {
                okcnt+=1.0;  // モデルがOKを出した数を数えておく
            }
            modelData.getEnsembleResults().add(md);
            Date eddate = new Date();
            logger.debug(idx+",end,"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(eddate));
            logger.debug(idx+",time,"+(eddate.getTime()-stdate.getTime()));
            idx = idx+1;
        }

        // 複数のモデルでOKが出たモデルの数を数えておき，閾値以上の正解数ならばOKを返す
        if(okcnt/(double)UnReliableModels.size()>EnsembleOkUnderLimit){
            modelData.setStatus(ModelStatus.OK);
            return true;
        }
        return false;
    }
}
