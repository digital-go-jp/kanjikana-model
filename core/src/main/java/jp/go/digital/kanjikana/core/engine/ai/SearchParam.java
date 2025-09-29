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

package jp.go.digital.kanjikana.core.engine.ai;

import jp.go.digital.kanjikana.core.Resources;

/**
 * 推論用のパラメタを保持する
 */
public final class SearchParam {
    private final int max_len;
    private final int beam_width;
    private final int n_best;
    private final boolean parallel_enabled;
    private final int parallelism;

    private final static int DEF_MAX_LEN = Integer.parseInt(Resources.getProperty(Resources.PropKey.AI_PM_MAXLEN));
    private final static int DEF_BEAM_WIDTH = Integer.parseInt(Resources.getProperty(Resources.PropKey.AI_PM_BEAMWIDTH));
    private final static int DEF_NBEST = Integer.parseInt(Resources.getProperty(Resources.PropKey.AI_PM_NBEST));

    private final static boolean DEF_PARALLEL_ENABLED= Boolean.parseBoolean(Resources.getProperty((Resources.PropKey.AI_PM_PARALLEL_ENABLED)));
    private final static int DEF_PARALLELISM = Integer.parseInt(Resources.getProperty(Resources.PropKey.AI_PM_PARALLELISM));

    /**
     * resources以下のファイル内で定義されたデフォルト値で初期化する
     */
    public SearchParam() throws Exception{
        this(DEF_MAX_LEN,DEF_BEAM_WIDTH,DEF_NBEST, DEF_PARALLEL_ENABLED, DEF_PARALLELISM);
    }

    /**
     * コンストラクタ
     * @param max_len 推論で作成する文字列の最大長さ
     * @param beam_width ビームサーチ幅，n_best以上にすること
     * @param n_best 推論で作成する最大数
     */
    public SearchParam(int max_len, int beam_width, int n_best, boolean parallel_enabled, int parallelism) throws Exception{
        this.max_len = max_len;
        this.beam_width = beam_width;
        this.n_best = n_best;
        this.parallel_enabled = parallel_enabled;
        this.parallelism = parallelism;
        if (this.parallelism<0){
            throw new Exception("parallelism_num is negative");
        }
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

    /**
     * 推論でマルチスレッドを使うかどうか
     * @return Trueならば使う、Falseならば使わない
     */
    public boolean isParallel_enabled() {
        return parallel_enabled;
    }

    /**
     * 推論でマルチスレッドを使う時、何並列で行うか
     * 0の時は自動でCPU数となる
     * @return CPU数
     */
    public int getParallelism() {
        return parallelism;
    }





}
