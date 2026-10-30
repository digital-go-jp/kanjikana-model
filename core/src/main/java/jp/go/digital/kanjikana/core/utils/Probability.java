package jp.go.digital.kanjikana.core.utils;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain;
import jp.go.digital.kanjikana.core.model.AbstModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * モデルでの確率を計算する
 *
 * @since v2.0
 */
public class Probability {
    private static final Logger logger = LogManager.getLogger(Probability.class);

    public static final double minProb = Double.parseDouble(Resources.getProperty(Resources.PropKey.PROB_MIN));
    public static final double maxProb = Double.parseDouble(Resources.getProperty(Resources.PropKey.PROB_MAX));
    //public static final double okLowerProb = Double.parseDouble(Resources.getProperty(Resources.PropKey.STRATEGY_EnsembleOkUnderLimit));
    public static final double model_default_min_prob = 1 - Double.parseDouble(Resources.getProperty(Resources.PropKey.MODEL_DEFAULT));
    public static final double alpha = 0.6;
    /**
     * リストの値をLNとした後の平均値を返す
     *
     * @param values 確率のリスト 0-1 の値
     * @return LogProbabilityの平均値
     */
    /*
    public static double meanOfLn(List<Double> values) {
        if (values == null || values.size() == 0) {
            return Math.log(model_default_min_prob); // LogProbabilityを表す、expで確率に戻す
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .peek(v -> {
                    if (v <= 0.0) throw new IllegalArgumentException("v <= 0: " + v);
                })
                .map(Math::log)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("values is empty"));
    }*/

    public static double meanOf(List<Double> values) {
        if (values == null || values.size() == 0) {
            return model_default_min_prob; // LogProbabilityを表す、expで確率に戻す
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .peek(v -> {
                    if (v <= 0.0) {
                        throw new IllegalArgumentException("v <= 0: " + v);
                    }
                })
                .average()
                .orElseThrow(() -> new IllegalArgumentException("values is empty"));
    }


    /**
     * Length Normalization（Google NMT 方式）で確率を計算する
     * @param values Probabilityの配列
     * @return 合計確率
     */
    /*
    public static double lengthNormalizedScore(List<Double> values) {

        List<Double> logProbs = values.stream()
                .map(p -> Math.log(p))
                .collect(Collectors.toList());


        // T = 長さ
        int T = logProbs.size();

        // log_probability の合計
        double logSum = 0.0;
        for (double v : logProbs) {
            logSum += v;
        }

        // Length Penalty (Google NMT)
        double lengthPenalty = Math.pow(5.0 + T, alpha) / Math.pow(6.0, alpha);

        // score
        return Math.exp(logSum / lengthPenalty);
    }*/


    /**
     * モデルで計算された単語、文字ごとの確率を掛け合わせて、入力された漢字とかなの一致確率とする
     *
     * @since v2.0
     */
    public static double getModelProbability(AbstModel model, ResultEngineParts now) {
        //double probability = model_default_min_prob;
        //if (now.getAttr().getProb() == null) {
        if(!model.isAiModel()) {
            if (now.getEngine() != null) {
                // 単純な辞書モデルの場合には、あらかじめ計算しておいたモデルとエンジンの組み合わせの確率に置き換える
                // 求めた確率を積算していくと、単語が多いほど不利なので、平均値にする
                //String engine = now.getEngine().getClass().getSimpleName();
                //String model = this.model.getSimpleName();
                //double p = Double.parseDouble(Resources.getProperty(Resources.getPropKey(model.getClass().getSimpleName(), engine)));
                /*
                if (now.isOk()) { // 漢字とカナ（単語や文字単位）が一致の時には
                    p = okLowerProb + (maxProb - okLowerProb) * p;
                    probability = p;

                }*/
                /*else { // NGなので確率を逆にする
                    //prob*=(1.0-probBase);
                    p = 1.0 - p;
                    p = minProb + (okLowerProb - minProb) * p;
                }
                probability = p;*/
                //logger.debug("probability;"+model.getClass().getSimpleName()+","+Resources.getModelProperty(model.getClass().getSimpleName()));
                //double weight = Double.parseDouble(Resources.getModelProperty(model.getClass().getSimpleName()));
                if(now.isOk()){
                    //return maxProb * weight;
                    return 1;
                }else{
                    //return minProb ;
                    return 0;
                }

            }
            return 0;
        } else { // AI
            double p = now.getAttr().getProb().doubleValue(); // pは0-1の値
            /*
            if (now.isOk()) { // OKの場合には下限50％になるように調整
                p = okLowerProb + (maxProb - okLowerProb) * p;
            } else { // NGの場合には上限50％になるように調整
                p = minProb + (okLowerProb - minProb) * p;
            }*/
            // 辞書やAIごとに求められている確率をかけていく
            //prob*=p;
            //probability = meanOf(Arrays.asList(p));
            //probability = lengthNormalizedScore(Arrays.asList(p), 0.6);
            //probability = p;
            return p;
        }
        //return probability;
    }


    /**
     * maxProb,minProbを超えないようにカットする
     * @param prob
     * @return
     */
    public static int modifyProb(int prob){
        int max=(int)(maxProb*100);
        int min=(int)(minProb*100);
        if (prob >max){
            return max;
        }else if (prob < min){
            return min;
        }else{
            return prob;
        }
    }

    /**
     * maxProb,minProbを超えないようにカットする
     * @param prob
     * @return
     */
    public static double modifyProb(double prob){
        if (prob >maxProb){
            return maxProb;
        }else if (prob < minProb){
            return minProb;
        }else{
            return prob;
        }
    }
}
