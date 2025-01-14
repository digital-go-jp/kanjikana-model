/*
 * MIT License
 *
 * Copyright (c) 2024-2025 デジタル庁
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

package jp.go.digital.kanjikana.core.executor;

import java.util.List;

/**
 * 漢字カナ突合モデルの実行用パラメタを保持するクラス
 */
public final class Params {
    private final int source_idx;
    private final int target_idx;
    private final Separator separator;
    private final String sep;
    private final List<String> lines;
    private final String header;

    /**
     * コンストラクタ
     * CSVやTSVファイル形式で，漢字姓名とカナ姓名を入力する
     * @param hasHeader ファイルがヘッダを持っているかどうか
     * @param source_idx 漢字姓名の行内での位置，先頭が０
     * @param target_idx カナ姓名の行内での位置，先頭が０
     * @param separator ファイルの区切り，TSVもしくはCSV
     * @throws Exception 一般的なエラー
     */
    /*
    private void init(boolean hasHeader, int source_idx, int target_idx, Separator separator ) throws Exception{
        if(source_idx <0){
            throw new Exception("source_idx is negative value");
        }
        if(target_idx <0){
            throw new Exception("target_idx is negative value");
        }
        if(source_idx == target_idx){
            throw new Exception("source_idx == target_idx exception");
        }
        this.source_idx = source_idx;
        this.target_idx = target_idx;
        this.separator = separator;
        if (separator == Separator.CSV) {
            this.sep = ",";
        } else {
            this.sep = "\t";
        }
        this.hasHeader = hasHeader;
    }

     */

    /**
     * ファイルのセパレータ形式
     */
    public enum Separator {
        CSV,
        TSV
    }

    /**
     * コンストラクタ
     * CSVやTSVファイル形式で，漢字姓名とカナ姓名を入力する
     * すでに読み込み済みの場合にはこちら
     * @param header ファイルのヘッダ ない場合にはnull
     * @param source_idx 漢字姓名の行内での位置，先頭が０
     * @param target_idx カナ姓名の行内での位置，先頭が０
     * @param separator ファイルの区切り，TSVもしくはCSV
     * @param lines ファイルに格納されている行のリスト
     * @throws Exception 一般的なエラー
     */
    public Params(String header, int source_idx, int target_idx, Separator separator, List<String> lines) throws Exception {
        /*
        if(source_idx <0){
            throw new Exception("source_idx is negative value");
        }
        if(target_idx <0){
            throw new Exception("target_idx is negative value");
        }
        */
        if(source_idx == target_idx){
            throw new Exception("source_idx == target_idx exception");
        }
        this.source_idx = source_idx;
        this.target_idx = target_idx;
        this.separator = separator;
        if (separator == Separator.CSV) {
            this.sep = ",";
        } else {
            this.sep = "\t";
        }
        this.header = header;
        if(lines == null){
            throw new Exception("arguments of input is null pointer exception");
        }
        this.lines=lines;
    }

    /**
     * 入力ファイルの何番目に漢字が入っているか
     * @return 漢字の番号　０オリジン，−1の時は無効
     */
    public int getSource_idx() {
        return source_idx;
    }

    /**
     * 入力ファイルの何番目にかなが入っているか
     * @return カナの番号　０オリジン，−1の時は無効
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

    public Separator getSeparator(){
        return separator;
    }
    public List<String> getLines(){
        return lines;
    }

    public String getHeader(){
        return this.header;
    }


    /**
     * ファイルの1行を与えて，漢字とカナを取得する
     * @param s ファイル内の1行
     * @return 漢字，カナの文字列配列  ret[0]:漢字，ret[1]:カナ
     * @throws Exception kanji_idx,kana_idxの入力エラー
     */
    public String[] getKanjiKana(String s) throws Exception {
        String[] items = s.split(getSep());
        String[] ret = new String[2];
        if (getSource_idx() >= items.length) {
            throw new Exception("kanji_index is not valid value,kanji_idx;" + getSource_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        ret[0] = items[getSource_idx()]; // check_params.getKanji_idx()とあわせる
        if (getTarget_idx() >= items.length) {
            throw new Exception("kana_index is not valid value,kana_idx;" + getTarget_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        ret[1] = items[getTarget_idx()]; // check_params.getKana_idx()とあわせる
        return ret;
    }

    /**
     * ファイルの1行を与えて，漢字を取得する
     * @param s ファイル内の1行
     * @return 漢字
     * @throws Exception kanji_idxの入力エラー
     */
    public String getKanji(String s) throws Exception {
        String[] items = s.split(getSep());
        if (getSource_idx() >= items.length) {
            throw new Exception("kanji_index is not valid value,kanji_idx;" + getSource_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        return items[getSource_idx()]; // check_params.getKanji_idx()とあわせる
    }

    /**
     * ファイルの1行を与えて，カナを取得する
     * @param s ファイル内の1行
     * @return カナの文字列
     * @throws Exception kana_idxの入力エラー
     */
    public String getKana(String s) throws Exception {
        String[] items = s.split(getSep());
        if (getTarget_idx() >= items.length) {
            throw new Exception("kana_index is not valid value,kana_idx;" + getTarget_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        return items[getTarget_idx()]; // check_params.getKana_idx()とあわせる
    }
}
