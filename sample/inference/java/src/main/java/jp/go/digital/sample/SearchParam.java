package jp.go.digital.sample;

/**
 * 推論用のパラメタを保持する
 */
public final class SearchParam {
    private final int max_len;
    private final int beam_width;
    private final int n_best;


    /**
     * コンストラクタ
     * @param max_len 推論で作成する文字列の最大長さ
     * @param beam_width ビームサーチ幅，n_best以上にすること
     * @param n_best 推論で作成する最大数
     */
    public SearchParam(int max_len, int beam_width, int n_best){
        this.max_len = max_len;
        this.beam_width = beam_width;
        this.n_best = n_best;
    }

    /**
     * 推論で作成する文字列の最大長さ
     * @return 推論で作成する文字列の最大長さ
     */
    public int getMax_len() {
        return max_len;
    }

    /**
     * ビームサーチ幅，n_best以上にすること
     * @return ビームサーチ幅
     */
    public int getBeam_width() {
        return beam_width;
    }

    /**
     * 推論で作成する最大数
     * @return 推論で作成する最大数
     */
    public int getN_best() {
        return n_best;
    }
}
