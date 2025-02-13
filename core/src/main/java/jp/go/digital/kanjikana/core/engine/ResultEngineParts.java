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

import java.io.Serializable;

/**
 * リスト形式で結果を作成する。
 * このクラスは単語，文字単位の一致結果
 */
public final class ResultEngineParts implements Serializable {

    public enum Type {
        OK,  // 漢字とカナがマッチ
       // NG,
        NOT_FOUND // 漢字とカナが一致せず
    }
    private final Type type;

    private final String kanji;

    private final String kana;

    private String kanji_orig;
    private String kana_orig;
    private int kanji_begin_idx;
    private int kanji_end_idx;
    private int kana_begin_idx;
    private int kana_end_idx;

    /**
     * 使用したエンジンがわかるようにクラスを保持する
     */
    private Class engine;

    /**
     * 使用した辞書がわかるように辞書クラスを保持する
     */
    private final Class dict;

    private final ResultAttr resultAttr;


    // 辞書とマッチした、一文字や単語単位での、一つ後ろの結果、 リストでつなげていく
    private ResultEngineParts nextResult;

    // 辞書とマッチした，単語，文字単位の結果，一つ前の結果
    private ResultEngineParts prevResult;

    public void setPrevResult(ResultEngineParts prevResult) {
        this.prevResult = prevResult;
    }

    public ResultEngineParts getPrevResult() {
        return prevResult;
    }

    public void setNextResult(ResultEngineParts nextResult) {
        this.nextResult = nextResult;
    }

    public ResultEngineParts getNextResult() {
        return nextResult;
    }

    public Type getType() {
        return type;
    }

    public boolean isOk(){
        return type==Type.OK;
    }

    public String getKanji() {
        return kanji;
    }

    public String getKana() {
        return kana;
    }

    public ResultAttr getAttr() {
        return resultAttr;
    }

    public Class getEngine() {
        return engine;
    }

    public Class getDict() {
        return dict;
    }

    public void setEngine(Class engine){
        this.engine = engine;
    }

    public String getKanji_orig() {
        return kanji_orig;
    }

    public String getKana_orig() {
        return kana_orig;
    }

    public int getKanji_begin_idx() {
        return kanji_begin_idx;
    }

    public int getKanji_end_idx() {
        return kanji_end_idx;
    }

    public int getKana_begin_idx() {
        return kana_begin_idx;
    }

    public int getKana_end_idx() {
        return kana_end_idx;
    }

    /**
     * @param kanji_orig 元の文字列
     * @param kanji_begin_idx kanjiのkanji_origでの開始位置 substringの引数参照
     * @param kanji_end_idx kanjiのkanji_origでの終了位置 substringの引数参照
     * @param kana_orig 元の読み仮名文字列
     * @param kana_begin_idx　kanaのkana_origでの開始位置 substringの引数参照
     * @param kana_end_idx kanaのkana_origでの終了位置 substringの引数参照
     */
    public void setCharParams(String kanji_orig, int kanji_begin_idx, int kanji_end_idx, String kana_orig, int kana_begin_idx, int kana_end_idx){
        this.kanji_orig = new String(kanji_orig);
        this.kanji_begin_idx = kanji_begin_idx;
        this.kanji_end_idx = kanji_end_idx;
        this.kana_orig = new String(kana_orig);
        this.kana_begin_idx = kana_begin_idx;
        this.kana_end_idx = kana_end_idx;
    }

    /**
     * 漢字とカナでマッチしたものを保持しておく
     * @param kanji_orig 元の文字列
     * @param kanji_begin_idx kanjiのkanji_origでの開始位置 substringの引数参照
     * @param kanji_end_idx kanjiのkanji_origでの終了位置 substringの引数参照
     * @param kanji チェックした漢字単語，漢字単語の一部
     * @param kana_orig 元の読み仮名文字列
     * @param kana_begin_idx　kanaのkana_origでの開始位置 substringの引数参照
     * @param kana_end_idx kanaのkana_origでの終了位置 substringの引数参照
     * @param kana　チェックした読み，読みの一部
     * @param resultAttr　マッチした辞書の属性
     * @param engine マッチと判定したクラス
     * @param dict 辞書でマッチした辞書のクラス
     */
    public ResultEngineParts(Type type, String kanji_orig, int kanji_begin_idx, int kanji_end_idx, String kanji, String kana_orig, int kana_begin_idx, int kana_end_idx, String kana, ResultAttr resultAttr, Class engine, Class dict){
        this.type = type;
        this.kanji_orig = new String(kanji_orig);
        this.kanji_begin_idx = kanji_begin_idx;
        this.kanji_end_idx = kanji_end_idx;
        this.kanji = new String(kanji);
        this.kana_orig = new String(kana_orig);
        this.kana_begin_idx = kana_begin_idx;
        this.kana_end_idx = kana_end_idx;
        this.kana = new String(kana);
        this.resultAttr = resultAttr;
        this.engine = engine;
        this.dict = dict;
    }

    /**
     * 漢字とカナでマッチしたものを保持しておく
     * @param kanji チェックした漢字単語，漢字単語の一部
     * @param kana　チェックした読み，読みの一部
     * @param resultAttr　マッチした辞書の属性
     * @param engine マッチと判定したクラス
     * @param dict 辞書でマッチした辞書のクラス
     */
    public ResultEngineParts(Type type, String kanji, String kana, ResultAttr resultAttr, Class engine, Class dict){
        this(type,kanji,0,kanji.length(),kanji,kana,0,kana.length(),kana, resultAttr,engine,dict);
    }

    public ResultEngineParts(String kanji, String kana){
        this(Type.NOT_FOUND,kanji,kana,new ResultAttr(), null,null);
    }

    public ResultEngineParts(ResultEngineParts res){
        this(res.type, res.kanji, res.kana, res.resultAttr, res.engine, res.dict);
        if(res.nextResult!=null) {
            this.nextResult = res.nextResult;
        }
        if(res.prevResult!=null) {
            this.prevResult = res.prevResult;
        }
    }
}
