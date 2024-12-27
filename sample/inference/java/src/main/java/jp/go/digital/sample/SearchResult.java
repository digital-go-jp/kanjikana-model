package jp.go.digital.sample;

/**
 * 推論結果１つを保持
 */
public final class SearchResult {
    private final String predict;
    private final double probability;

    /**
     * コンストラクタ
     * @param predict 推論した文字列
     * @param probability 推論した文字列の確率
     */
    public SearchResult(String predict, double probability){
        this.predict = predict;
        this.probability = probability;
    }

    /**
     * 推論した文字列を返す
     * @return 推論した文字列
     */
    public String getPredict() {
        return predict;
    }

    /**
     * 推論した文字列の確率を返す
     * @return 確率，１＝１００％
     */
    public double getProbability() {
        return probability;
    }

    /**
     * このクラスの文字列表現
     * @return 文字列
     */
    public String toString(){
        return "predict;"+this.predict+":probability;"+probability;
    }
}
