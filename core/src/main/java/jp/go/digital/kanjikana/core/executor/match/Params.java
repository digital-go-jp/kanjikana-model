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

package jp.go.digital.kanjikana.core.executor.match;

import java.util.List;

/**
 * 漢字カナ突合モデルの実行用パラメタを保持するクラス
 */
public final class Params {
    private final int source_idx;
    private final int target_idx;
    private final Separator separator;
    private final String sep;
    private List<String> lines;
    private final boolean hasHeader;

    /**
     * コンストラクタ
     * CSVやTSVファイル形式で，漢字姓名とカナ姓名を入力する
     * @param hasHeader ファイルがヘッダを持っているかどうか
     * @param source_idx 漢字姓名の行内での位置，先頭が０
     * @param target_idx カナ姓名の行内での位置，先頭が０
     * @param separator ファイルの区切り，TSVもしくはCSV
     * @throws Exception 一般的なエラー
     */
    public Params(boolean hasHeader, int source_idx, int target_idx, Separator separator ) throws Exception{
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

    /**
     * ファイルのセパレータ形式
     */
    enum Separator {
        CSV,
        TSV
    }

    /**
     * コンストラクタ
     * CSVやTSVファイル形式で，漢字姓名とカナ姓名を入力する
     * すでに読み込み済みの場合にはこちら
     * @param hasHeader ファイルがヘッダを持っているかどうか
     * @param source_idx 漢字姓名の行内での位置，先頭が０
     * @param target_idx カナ姓名の行内での位置，先頭が０
     * @param separator ファイルの区切り，TSVもしくはCSV
     * @param lines ファイルに格納されている行のリスト
     * @throws Exception 一般的なエラー
     */
    public Params(boolean hasHeader, int source_idx, int target_idx, Separator separator, List<String> lines) throws Exception {
        this(hasHeader, source_idx,target_idx,separator);
        if(lines == null){
            throw new Exception("arguments of input is null pointer exception");
        }
        this.lines=lines;
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

    public Separator getSeparator(){
        return separator;
    }
    public List<String> getLines(){
        return lines;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }
}
