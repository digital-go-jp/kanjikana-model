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

package jp.go.digital.kanjikana.core.model;

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;

/**
 * implで定義するモデルの抽象クラス
 *
 */
public abstract class AbstModel implements ModelIF{
    private final EngineIF engine;

    public AbstModel(EngineIF engine){
        this.engine = engine;
    }

    protected EngineIF getEngine(){
        return engine;
    }

    @Override
    public boolean isValidModel(){
        return engine.isValidEngine();
    }

    /**
     * Topノードを見つける
     * @param res　結果のリスト形式のどこかのノード
     * @return 結果のリスト形式のトップのノード
     */
    protected ResultEngineParts getTopResult(ResultEngineParts res){
        ResultEngineParts now = res;
        while(true){
            if(now.getPrevResult()==null){
                return now;
            }
            now = now.getPrevResult();
        }
    }

    /**
     * 末尾のノードを見つける
     * @param res　結果のリスト形式のどこかのノード
     * @return 結果のリスト形式の末尾のノード
     */
    protected ResultEngineParts getLastResult(ResultEngineParts res){
        ResultEngineParts now = res;
        while(true){
            if(now.getNextResult()==null){
                return now;
            }
            now = now.getNextResult();
        }
    }

    /**
     * ステータスをセットする
     * @param modelData 結果データ
     */
    protected void setStatus(ModelData modelData){
        ResultEngineParts now=modelData.getTopResult();
        modelData.setStatus(ModelStatus.OK);
        while(true){
            if(!now.isOk()){
                modelData.setStatus(ModelStatus.NG);
                return;
            }
            now=now.getNextResult();
            if(now==null){
                return;
            }
        }
    }

    /**
     * modelResultを見て，調べる漢字，カナの部分を絞る，
     * WordEngineのものは残す、がCharEngineのものはNGとしてくっつけて再検査
     * @param kanji_item 漢字姓名　「山田　太郎」
     * @param kana_item　カナ姓名　「ヤマダ　タロウ」
     * @param modelData 今までの判定結果
     * @return 判定結果をモディファイした結果を返す
     */
    protected abstract ModelData exec(String kanji_item, String kana_item, ModelData modelData)throws Exception;

    /**
     * 漢字とカナ（単語単位とは限らない，部分文字列をある）を受け取り，それが一致しているかどうかを返す
     * @param kanji_str 漢字姓名もしくはその一部
     * @param kana_str カナ姓名もしくはその一部
     * @return 判定結果
     * @throws Exception 一般的なエラー
     */
    protected abstract ResultEngineParts run_sub(String kanji_str, String kana_str)throws Exception;
}
