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

package jp.go.digital.kanjikana.core.engine.foreigner;


/**
 * 入力パラメタ一覧
 */
public final class ParamsForeigner  {

    private final int source_idx;
    private final int target_idx;

    private final String sep;
    /**
     * チェック度合い
     */
    private final String check;

    public static class Check{
        public static final String strict = "strict";
        public static final String standard = "standard";
        public static final String loose = "loose";
    }

    private final boolean raw;
    private final String infile;

    /**
     * チェック用パラメタ
     * @param enf 英語名のカラム番号 0オリジン
     * @param jaf 日本語名のカラム番号 0オリジン
     * @param check　チェック方法　strict or standard or loose ,   TODO standard以外未検査
     * @param raw 対応関係をローレベルで表示 TODO true 以外未検査
     * @param sep 入力行のセパレータ tsv or csv
     * @param infile 入力ファイル名
     */
    public ParamsForeigner(int enf, int jaf, String check, boolean raw, String sep, String infile) throws Exception{
        this.source_idx = enf;
        this.target_idx = jaf;
        this.sep = sep;

        this.check = check;
        this.raw = raw;
        this.infile = infile;
    }

    /**
     * 入力ファイル名
     * @return ファイル名
     */
    public String getInfile() {
        return infile;
    }


    public String getCheck() {
        return check;
    }

    public boolean isRaw() {
        return raw;
    }


    /**
     * 入力ファイルの何番目に漢字が入っているか
     * @return 漢字の番号　０オリジン
     */
    public int getSource_idx() {
        return source_idx;
    }

    /**
     * 入力ファイルの何番目にかなが入っているか
     * @return カナの番号　０オリジン
     */
    public int getTarget_idx() {
        return target_idx;
    }

    /**
     * 入力ファイルのセパレータ　
     * @return csv or tsv
     */
    public String getSep() {
        return sep;
    }
}
