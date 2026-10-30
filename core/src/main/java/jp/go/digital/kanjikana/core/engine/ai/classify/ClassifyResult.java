package jp.go.digital.kanjikana.core.engine.ai.classify;

/**
 * 2値分類の結果を保持する
 */
public class ClassifyResult {
    private final float probability;
    private final boolean isOk;
    private final String kanji;
    private final String kana;


    /**
     * 漢字とカナが一致しているかどうかのモデルによる2値分類の結果を保持する
     * @param kanji 漢字姓名 単語単位
     * @param kana カナ姓名 単語単位
     * @param probability 確率
     */
    public ClassifyResult(String kanji, String kana, float probability){
        this.isOk = probability >= 0.5;
        this.probability = probability;
        this.kanji = kanji;
        this.kana = kana;
    }

    /**
     * 2値分類の結果、漢字とカナが一致
     * @return 判定結果
     */
    public boolean isOk() {
        return isOk;
    }

    /**
     * 漢字とカナの一致確率
     * @return 0-1の値
     */
    public double getProbability() {
        return probability;
    }

    public String getKanji() {
        return kanji;
    }

    public String getKana() {
        return kana;
    }
}
