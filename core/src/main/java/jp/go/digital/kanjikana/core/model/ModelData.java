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

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.utils.Probability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * モデルで計算した結果を保持する
 */
public class ModelData implements Serializable {
    protected static final double EnsembleOkUnderLimit= Double.parseDouble(Resources.getProperty(Resources.PropKey.STRATEGY_EnsembleOkUnderLimit)); // アンサンブルでOKを出したモデルの割合の最小値

    /**
     * 単語や文字単位で分割し，判定した結果をリンクリストで保持する。そのトップノード
     */
    private ResultEngineParts topResult=null;

    private ModelStatus modelStatus = ModelStatus.NG;

    // 順次モデルで計算するが，OKだった時のモデルクラスを保持する
    private AbstModel model=null;

    // 入力の漢字
    private final String kanji;
    // 入力のカナ
    private final String kana;

    // 漢字とカナの一致確率
    private double modelProbability;

    private final List<ModelData> ensembleResult = new ArrayList<>(); // アンサンブル学習の結果が入る。アンサンブルでない時は空のリスト


    public ModelData copy(){
        ModelData md = new ModelData(new String(this.kanji),new String(this.kana));
        for (ModelData d: this.ensembleResult){
            md.ensembleResult.add(d);
        }
        if (this.topResult!=null){
            md.topResult= new ResultEngineParts(this.topResult);
        }

        md.modelStatus = this.modelStatus;
        md.model = this.model;
        md.modelProbability = this.modelProbability;

        return md;
    }


    /**
     * モデルで計算した結果を保持する。ステータスがNGの時はこのコンストラクタ
     * @param kanji　入力する漢字姓名もしくはその一部　「山田　太郎」
     * @param kana　入力するカタカナ姓名もしくはその一部　　「ヤマダ　タロウ」
     */
    public ModelData( String kanji, String kana) {
        this.kanji = kanji;
        this.kana = kana;
    }

    /**
     * モデルで計算した結果を保持
     * @param kanji　入力する漢字姓名もしくはその一部　「山田　太郎」
     * @param kana　入力するカタカナ姓名もしくはその一部　　「ヤマダ　タロウ」
     * @param modelStatus 計算結果ステータス
     */

    /**
     * モデルで計算された単語、文字ごとの確率を掛け合わせて、入力された漢字とかなの一致確率とする
     * @since v2.0
     */
    private double calcModelProbability() throws Exception {
        if (model==null) {
            throw new IllegalStateException("model is null");
        }
        ResultEngineParts now=topResult;
        //return Probability.getModelProbability(model,now);

        List<Double> probList = new ArrayList<>();
        boolean breakTrue=false;
        for(int j=0;j<Integer.MAX_VALUE;j++){
            if(now == null){
                breakTrue=true;
                break;
            }
            double prob = Probability.getModelProbability(model,now);
            now.getAttr().setProb(prob);
            probList.add(prob);
            now=now.getNextResult();
        }
        if(!breakTrue){
            throw new RuntimeException("infinite loop,("+this.getClass().getSimpleName()+"),"+this.kanji+","+this.kana);
        }


        double prob = probList.stream().reduce(1.0, (a, b) -> a * b);

        //return Probability.lengthNormalizedScore(probList);
        return prob;
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
    public void setTopResult(ResultEngineParts topResult) throws Exception {
        if(topResult==null || topResult.getKanji()==null || topResult.getKana()==null || topResult.getKanji().length()==0 || topResult.getKana().length()==0){
            throw new IllegalStateException("topResult is null");
        }
        this.topResult = topResult;
        this.modelProbability = this.calcModelProbability();
    }

    /**
     * 計算済みの結果を，ResultEnginePartsのリンクリストで表現するが，そのリンクリストをクリアする
     */
    public void resetTopResult(){

        //this.setTopResult(new ResultEngineParts("",""));
        this.topResult=null;
    }

    /**
     * 結果ステータス
     * @return OK or NG
     */
    public ModelStatus getStatus() {
        return this.modelStatus;
    }

    /**
     * 漢字とカナの突合結果ステータスだけをセット
     * @param modelStatus　ステータス
     */
    public void setStatus(ModelStatus modelStatus) {
        if(modelStatus == null){
            throw new NullPointerException("modelStatus cannot be null");
        }
        this.modelStatus = modelStatus;
    }

    /**
     * 漢字とカナの突合結果
     */
    public void setProbability() throws Exception {
        if (!ModelStatus.isError(this.modelStatus)) {
            this.modelProbability = this.calcModelProbability();
            if (this.modelProbability < EnsembleOkUnderLimit) {
                this.modelStatus = ModelStatus.NG;
            } else {
                this.modelStatus = ModelStatus.OK;
            }
        }
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
    public String getResult() throws Exception {
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
        return ResultNoteMaker.basename(model.getClass());
    }

    /**
     * 突合でOKがでたモデルクラスをセットする
     * @param model モデルのクラス
     */
    public void setModel(AbstModel model) {
        this.model = model;
    }

    public String getKanji() {
        return kanji;
    }

    public String getKana() {
        return kana;
    }

    public double getModelProbability() {
        return modelProbability;
    }

    /**
     *  StrategyEnsembleモデルを用いた結果があればそれを返す
     * @return Ensembleモデルの結果を，それぞれのモデルごとの結果をリスト形式で返す
     */
    //public List<ModelData> getEnsembleResults(){ return ensembleResult;}

    public void resetEnsembleResult(){
        this.ensembleResult.clear();
    }

    public boolean isEnsembleResultEmpty(){
        return ensembleResult.isEmpty();
    }

    /**
     * 一つのストラテジーでの計算結果をEnsembleのリストに追加する
     * 単語単位でチェックした結果の確率のLength Normalization（Google NMT 方式）で全体確率とする
     * @param modelData 計算結果
     * @since v2.0
     */
    public void addEnsembleResult(ModelData modelData) {
        ensembleResult.add(modelData);


        // 全体の結果としてトップのMpdelDataの判定を更新する

        List<Double> probList = new ArrayList<>();
        for(ModelData md : ensembleResult){
            probList.add(md.getModelProbability());
        }
        double prob = probList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        //if(prob>Probability.maxProb){
        //    prob=Probability.maxProb;
        //}
        if(prob>=EnsembleOkUnderLimit){
            this.setStatus(ModelStatus.OK);// TopもModelDataのProbabilityをEnsembleの結果としてセットする
        }else{
            this.setStatus(ModelStatus.NG);// TopもModelDataのProbabilityをEnsembleの結果としてセットする
        }
        this.modelProbability=prob; // TopもModelDataのProbabilityをEnsembleの結果としてセットする
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
    private String getEnesemble() throws Exception{
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
