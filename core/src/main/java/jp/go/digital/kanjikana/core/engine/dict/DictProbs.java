package jp.go.digital.kanjikana.core.engine.dict;

import jp.go.digital.kanjikana.core.Resources;

/**
 * 辞書モデルで計算した際に、v1.xでは、True or Falseで判定していたが、v2,xからは0-1の連続値で出すようにするためのクラス
 * @since v2.0
 */
public class DictProbs {

    // 辞書モデルの辞書の頻度が、この頻度以下の場合には確率０として計算
    private static final int minFreq = Integer.parseInt(Resources.getProperty(Resources.PropKey.DIC_STATISTICS_MINFREQ));
    // 辞書モデルの辞書の頻度が、この頻度以上の場合には確率１として計算
    private static final int maxFreq = Integer.parseInt(Resources.getProperty(Resources.PropKey.DIC_STATISTICS_MAXFREQ));

    /**
     * 統計辞書の出現回数（頻度）を確率に変換する
     *
     * @param freq 統計辞書に入っている、出現回数
     * @return 確率
     */
    public static double calcStatisticsProbs(int freq){
        double prob = 0;
        if(freq >= maxFreq){
            prob=1;
        }else if(freq <= minFreq){
            prob=0;
        }else{
            prob=(double)(freq - minFreq) / (maxFreq - minFreq);
        }
        return prob;
    }
}
