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

import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.executor.match.strategy.AbstStrategy;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.model.impl.*;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * 簡易モデル
 * 時間のかからない簡易モデルをシングルトンで定義する。
 * 辞書単語とのマッチング＋異体字チェック，外国人モデルのみ
 */
public class StrategyBasic extends AbstStrategy {
    private static StrategyIF sb=null;
    private final Logger logger = LogManager.getLogger(StrategyBasic.class);

    protected StrategyBasic() throws Exception{
        setReliableModels(Arrays.asList(new IReliableWordModel(), new FWordModel(),new INandokuWordModel()));
    }

    /**
     * 簡易モデルを返す
     * @return 簡易モデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyBasic();
        }
        return sb;
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
    public boolean modelCheck(ModelData modelData, String kanji, String kana) throws Exception{
        modelData.setTopResult(new ResultEngineParts("","")); // reset
        for (ModelIF model : models) {
            modelData = model.run(kanji,kana, modelData);

            logger.debug(model.getClass()+",kanji="+kanji+",isOK="+modelData.isOk());

            if (modelData.isOk()) {
                modelData.setModel(model.getClass());
                return true;
            }
        }
        if (modelData.isOk()) {
            return true;
        }
        return false;
    }
}
