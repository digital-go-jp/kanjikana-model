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

package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.Resources;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 辞書の1レコードを保持する。統計情報と，辞書の由来を
 */
public final class ResultAttr implements Serializable {
    @Serial
    private static final long serialVersionUID = -8054973273447626011L;
    public static final double model_default_min_prob = 1 - Double.parseDouble(Resources.getProperty(Resources.PropKey.MODEL_DEFAULT));

    private Double prob; // 確率
    private  final List<String> dictTypes;

    private String remarks;


    /**
     * 出力の属性を作成する。
     * 不一致の際のコンストラクタ。確率は０で初期化する。
     */
    public ResultAttr(){
        this(model_default_min_prob,null,null);
    }


    /**
     * 出力の属性を作成する
     *
     * @param prob 確率 0-1
     * @param remarks　判定結果の備考 外国人モデルの判定結果など
     */
    /*
    public ResultAttr(double prob, String remarks){
        this.remarks = remarks;
        //this.freq = 0;
        this.prob = prob;
        this.dictTypes = new ArrayList<>();
    }*/


    /**
     * 出力の属性を作成する。単語や文字単位での判定結果。
     * 統計モデル、AIモデルなど確率として出力されるものは確率を入力、辞書モデルなどTrue/Falseで判断されるものはnullをProbabilityに入れる。
     * あとで、ModelData内で、Resourcesで定義した確率に置き換える
     * @param prob 確率 0-1
     * @param dictNames 辞書の名前
     * @param remarks　判定結果の備考 外国人モデルの判定結果など
     */

    public ResultAttr(Double prob, List<String> dictNames, String remarks) {
        //this.prob= DictProbs.calcStatisticsProbs(freq);
        if(remarks==null){
            this.remarks = "";
        }else{
            this.remarks = remarks;
        }
        this.prob = prob;
        this.dictTypes = new ArrayList<>();
        if(dictNames != null) {
            for (String dictName : dictNames) {
                dictTypes.add(dictName);
            }
        }
    }

    public List<String> getDictNames() {
        return dictTypes;
    }

    public Double getProb() {
        return prob;
    }

    public void setProb(Double prob) {
        this.prob = prob;
    }

    public void setRemarks(String remarks) {this.remarks = remarks;}

    public String toText(){
        StringBuilder sb = new StringBuilder();

        //sb.append(freq);
        sb.append(prob);
        sb.append(":/");
        List<String> names = new ArrayList<>();
        for(String dic :dictTypes) {
            names.add(dic);
        }
        names.sort(Comparator.naturalOrder());
        for(String name:names){
            sb.append(name);
            sb.append("/");
        }
        sb.append(";");
        sb.append(remarks);
        return sb.toString();
    }

    public String toString(){
        return toText();
    }

}
