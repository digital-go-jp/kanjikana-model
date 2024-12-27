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

import jp.go.digital.kanjikana.core.engine.ResultEngineParts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * モデルで計算した結果を保持する
 */
public class ModelData implements Serializable {

    /**
     * 単語や文字単位で分割し，判定した結果をリンクリストで保持する。そのトップノード
     */
    private ResultEngineParts topResult;

    private ModelStatus modelStatus;

    // 順次モデルで計算するが，OKだった時のモデルクラスを保持する
    private Class model;

    // 入力の漢字
    private final String kanji;
    // 入力のカナ
    private final String kana;

    private List<ModelData> ensembleResult = new ArrayList<>(); // アンサンブル学習の結果が入る。アンサンブルでない時は空のリスト

    /**
     * モデルで計算した結果を保持する。ステータスがNGの時はこのコンストラクタ
     * @param kanji　入力する漢字姓名もしくはその一部　「山田　太郎」
     * @param kana　入力するカタカナ姓名もしくはその一部　　「ヤマダ　タロウ」
     */
    public ModelData(String kanji, String kana) {
        this.kanji = kanji;
        this.kana = kana;
        this.model = null;
        this.modelStatus = ModelStatus.NG;
        this.topResult = new ResultEngineParts("", "");
    }

    /**
     * モデルで計算した結果を保持
     * @param kanji　入力する漢字姓名もしくはその一部　「山田　太郎」
     * @param kana　入力するカタカナ姓名もしくはその一部　　「ヤマダ　タロウ」
     * @param modelStatus 計算結果ステータス
     */
    public ModelData(String kanji, String kana, ModelStatus modelStatus) {
        this.kanji = kanji;
        this.kana = kana;
        this.model = null;
        this.modelStatus = modelStatus;
        this.topResult = new ResultEngineParts("", "");
    }

    /**
     * 計算済みの結果を，ResultEnginePartsのリンクリストで表現するが，そのリンクリストのトップを返す
     * @return 結果のリンクリストのトップを返す
     */
    public ResultEngineParts getTopResult() {
        return topResult;
    }

    /**
     * 計算済みの結果を，ResultEnginePartsのリンクリストで表現するが，そのリンクリストのトップをセットする
     * @param topResult 結果のリンクリストのトップ
     */
    public void setTopResult(ResultEngineParts topResult) {
        this.topResult = topResult;
    }

    /**
     * 計算済みの結果を，ResultEnginePartsのリンクリストで表現するが，そのリンクリストをクリアする
     */
    public void resetTopResult(){
        this.topResult = new ResultEngineParts("","");
    }

    /**
     * 結果ステータス
     * @return OK or NG
     */
    public ModelStatus getStatus() {
        return this.modelStatus;
    }

    /**
     * 漢字とカナの突合結果ステータスをセット
     * @param modelStatus　結果
     */
    public void setStatus(ModelStatus modelStatus) {
        this.modelStatus = modelStatus;
    }

    /**
     * 漢字とカナの突合結果がOKかどうか
     * @return Trueの時は，漢字とカナが突合した
     */
    public boolean isOk() {
        return this.modelStatus == ModelStatus.OK;
    }

    /**
     * 漢字とかなの突合結果の文字列表現
     * @return 突合結果文字列表現
     */
    public String getResult() {
        String res = ResultNoteMaker.getText(topResult);
        if(ensembleResult.size()>0){
            res+=getEnesemble();
        }
        return res;
    }

    /**
     * 突合でOKがでたモデルクラス
     * @return クラスの文字列表現
     */
    public String getModel() {
        return ResultNoteMaker.basename(model);
    }

    /**
     * 突合でOKがでたモデルクラスをセットする
     * @param model モデルのクラス
     */
    public void setModel(Class model) {
        this.model = model;
    }

    public String getKanji() {
        return kanji;
    }

    public String getKana() {
        return kana;
    }

    /**
     *  StrategyEnsembleモデルを用いた結果があればそれを返す
     * @return Ensembleモデルの結果を，それぞれのモデルごとの結果をリスト形式で返す
     */
    public List<ModelData> getEnsembleResults(){ return ensembleResult;}

    /**
     * StrategyEnsembleモデルで計算した結果があれば，その結果を文字列表現で返す
     * @return Ensembleモデルの結果の文字列表現
     */
    private String getEnesemble(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(ModelData md: ensembleResult){
            sb.append("<");
            sb.append("|");
            sb.append(md.getModel());
            sb.append("|");
            sb.append(ResultNoteMaker.getText(md.topResult));
            sb.append(">");
        }
        sb.append("]");

        return sb.toString();
    }
}
