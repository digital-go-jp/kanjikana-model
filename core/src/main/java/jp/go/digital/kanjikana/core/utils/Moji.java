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

package jp.go.digital.kanjikana.core.utils;

import org.terasoluna.gfw.common.fullhalf.DefaultFullHalf;

import java.util.regex.Pattern;

/**
 * カタカナ姓名を正規化する
 * ひらがな，半角カタカナなどを全角カタカナに正規化。
 * 旧字や２つに分かれている半濁点なども修正
 */
public final class Moji {
    static final Pattern KINSOKU= Pattern.compile("[ァィゥェォッャュョヮンー　]");

    /**
     * 小書き文字など，単語の先頭にこない文字をチェックする。Charモデルで一文字ずつ切り出してチェックしている時に発生する
     * @param s　カタカナ姓名
     * @return 単語の先頭にこない文字の時にはFalse
     */
    public static boolean isKinsoku(String s){
        if(s!=null && s.length()>0){
            return KINSOKU.matcher(s.substring(0,1)).matches();
        }
        return true;
    }

    private static String ReplaceString(String s, String fm, String to){
        return new String(s).replace(fm,to);
    }

    /**
     * ２文字に分かれている濁点，半濁点を一文字に変換
     * @param s カタカナ姓名　「ヤマタ゛　タロウ」
     * @return 濁点を一文字にする　「ヤマダ　タロウ」　　タと゛をダへ修正
     */
    static String norm_dakuten(String s){
        s = ReplaceString(s, "ウ゛","ヴ");
        s = ReplaceString(s, "カ゛","ガ");
        s = ReplaceString(s, "キ゛","ギ");
        s = ReplaceString(s, "ク゛","グ");
        s = ReplaceString(s, "ケ゛","ゲ");
        s = ReplaceString(s, "コ゛","ゴ");
        s = ReplaceString(s, "サ゛","ザ");
        s = ReplaceString(s, "シ゛","ジ");
        s = ReplaceString(s, "ス゛","ズ");
        s = ReplaceString(s, "セ゛","ゼ");
        s = ReplaceString(s, "ソ゛","ゾ");
        s = ReplaceString(s, "タ゛","ダ");
        s = ReplaceString(s, "チ゛","ヂ");
        s = ReplaceString(s, "ツ゛","ヅ");
        s = ReplaceString(s, "テ゛","デ");
        s = ReplaceString(s, "ド゛","ド");
        s = ReplaceString(s, "ハ゛","バ");
        s = ReplaceString(s, "ヒ゛","ビ");
        s = ReplaceString(s, "フ゛","ブ");
        s = ReplaceString(s, "ヘ゛","ベ");
        s = ReplaceString(s, "ホ゛","ボ");
        return s;
    }

    /**
     * 小書き文字を大文字に変換
     * @param s　カタカナ姓名「ヤマダ　ジョウジ」
     * @return ヤマダ　ジヨウジ
     */
    static String norm_kogaki( String s){
        s = ReplaceString( s,"ㇷ゚","プ");
        
        s = ReplaceString( s,"ァ","ア");
        s = ReplaceString( s,"ィ","イ");
        s = ReplaceString( s,"ゥ","ウ");
        s = ReplaceString( s,"ェ","エ");
        s = ReplaceString( s,"ォ","オ");
        s = ReplaceString( s,"ッ","ツ");
        s = ReplaceString( s,"ャ","ヤ");
        s = ReplaceString( s,"ュ","ユ");
        s = ReplaceString( s,"ョ","ヨ");
        s = ReplaceString( s,"ヮ","ワ");

        s = ReplaceString( s,"ヵ","カ");
        s = ReplaceString( s,"ㇰ","ク");
        s = ReplaceString( s,"ヶ","ケ");
        s = ReplaceString( s,"ㇱ","シ");
        s = ReplaceString( s,"ㇲ","ス");
        s = ReplaceString( s,"ㇳ","ト");
        s = ReplaceString( s,"ㇴ","ヌ");
        s = ReplaceString( s,"ㇵ","ハ");
        s = ReplaceString( s,"ㇶ","ヒ");
        s = ReplaceString( s,"ㇷ","フ");

        s = ReplaceString( s,"ㇸ","ヘ");
        s = ReplaceString( s,"ㇹ","ホ");
        s = ReplaceString( s,"ㇺ","ム");
        s = ReplaceString( s,"ㇻ","ラ");
        s = ReplaceString( s,"ㇼ","リ");
        s = ReplaceString( s,"ㇽ","ル");
        s = ReplaceString( s,"ㇾ","レ");
        s = ReplaceString( s,"ㇿ","ロ");
        s = ReplaceString( s,"ヮ","ワ");
        return s;
    }

    /**
     * 全銀協文字で使えないものを変換　旧字カタカナなど
     * @param s カタカナ姓名　「ヤマダ　タカヲ」
     * @return ヤマダ　タカオ　　
     */
    static String norm_zengin(String s){
        s = ReplaceString(s, "ヲ", "オ");
        s = ReplaceString(s, "ヰ", "イ");
        s = ReplaceString(s, "ヱ", "エ");
        return s;
    }

    /**
     * ひらがなからカタカナへ変換
     * @param s　カタカナ姓名や漢字姓名でもひらがな名が含まれるもの　「山田　たろう」
     * @return カタカナ変換　「山田　タロウ」
     */
    static String norm_hirakata( String s )
    {
        s = ReplaceString( s,"ぁ","ァ");
        s = ReplaceString( s,"あ","ア");
        s = ReplaceString( s,"ぃ","ィ");
        s = ReplaceString( s,"い","イ");
        s = ReplaceString( s,"ぅ","ゥ");
        s = ReplaceString( s,"う","ウ");
        s = ReplaceString( s,"ぇ","ェ");
        s = ReplaceString( s,"え","エ");
        s = ReplaceString( s,"ぉ","ォ");
        s = ReplaceString( s,"お","オ");
        s = ReplaceString( s,"か","カ");
        s = ReplaceString( s,"が","ガ");
        s = ReplaceString( s,"き","キ");
        s = ReplaceString( s,"ぎ","ギ");
        s = ReplaceString( s,"く","ク");
        s = ReplaceString( s,"ぐ","グ");
        s = ReplaceString( s,"け","ケ");
        s = ReplaceString( s,"げ","ゲ");
        s = ReplaceString( s,"こ","コ");
        s = ReplaceString( s,"ご","ゴ");
        s = ReplaceString( s,"さ","サ");
        s = ReplaceString( s,"ざ","ザ");
        s = ReplaceString( s,"し","シ");
        s = ReplaceString( s,"じ","ジ");
        s = ReplaceString( s,"す","ス");
        s = ReplaceString( s,"ず","ズ");
        s = ReplaceString( s,"せ","セ");
        s = ReplaceString( s,"ぜ","ゼ");
        s = ReplaceString( s,"そ","ソ");
        s = ReplaceString( s,"ぞ","ゾ");
        s = ReplaceString( s,"た","タ");
        s = ReplaceString( s,"だ","ダ");
        s = ReplaceString( s,"ち","チ");
        s = ReplaceString( s,"ぢ","ヂ");
        s = ReplaceString( s,"っ","ッ");
        s = ReplaceString( s,"つ","ツ");
        s = ReplaceString( s,"づ","ヅ");
        s = ReplaceString( s,"て","テ");
        s = ReplaceString( s,"で","デ");
        s = ReplaceString( s,"と","ト");
        s = ReplaceString( s,"ど","ド");
        s = ReplaceString( s,"な","ナ");
        s = ReplaceString( s,"に","ニ");
        s = ReplaceString( s,"ぬ","ヌ");
        s = ReplaceString( s,"ね","ネ");
        s = ReplaceString( s,"の","ノ");
        s = ReplaceString( s,"は","ハ");
        s = ReplaceString( s,"ば","バ");
        s = ReplaceString( s,"ぱ","パ");
        s = ReplaceString( s,"ひ","ヒ");
        s = ReplaceString( s,"び","ビ");
        s = ReplaceString( s,"ぴ","ピ");
        s = ReplaceString( s,"ふ","フ");
        s = ReplaceString( s,"ぶ","ブ");
        s = ReplaceString( s,"ぷ","プ");
        s = ReplaceString( s,"へ","ヘ");
        s = ReplaceString( s,"べ","ベ");
        s = ReplaceString( s,"ぺ","ペ");
        s = ReplaceString( s,"ほ","ホ");
        s = ReplaceString( s,"ぼ","ボ");
        s = ReplaceString( s,"ぽ","ポ");
        s = ReplaceString( s,"ま","マ");
        s = ReplaceString( s,"み","ミ");
        s = ReplaceString( s,"む","ム");
        s = ReplaceString( s,"め","メ");
        s = ReplaceString( s,"も","モ");
        s = ReplaceString( s,"ゃ","ャ");
        s = ReplaceString( s,"や","ヤ");
        s = ReplaceString( s,"ゅ","ュ");
        s = ReplaceString( s,"ゆ","ユ");
        s = ReplaceString( s,"ょ","ョ");
        s = ReplaceString( s,"よ","ヨ");
        s = ReplaceString( s,"ら","ラ");
        s = ReplaceString( s,"り","リ");
        s = ReplaceString( s,"る","ル");
        s = ReplaceString( s,"れ","レ");
        s = ReplaceString( s,"ろ","ロ");
        s = ReplaceString( s,"ゎ","ヮ");
        s = ReplaceString( s,"わ","ワ");
        s = ReplaceString( s,"を","ヲ");
        s = ReplaceString( s,"ん","ン");
        s = ReplaceString( s,"ゐ","ヰ");
        s = ReplaceString( s,"ゑ","ヱ");
        return s;
    }

    /**
     * 不要な文字，成年後見人と成年被後見人を削除
     * @param s 漢字姓名　「成年後見人　山田太郎」
     * @return 山田太郎
     */
    static String omit(String s){
        s = ReplaceString(s, "成年後見人","");
        s = ReplaceString(s, "成年被後見人","");
        return s;
    }

    /**
     * 長音に似た文字を修正
     * @param s　漢字やカナ姓名　「ビリー　ジョエル」
     * @return 長音に似た横棒を「ー」に変換する
     */
    static String norm_chouon(String s){
        s = ReplaceString(s, "－","ー");
        s = ReplaceString(s, "-","ー");
        s = ReplaceString(s, "−","ー");
        s = ReplaceString(s, "‐","ー");
        s = ReplaceString(s, "‒","ー");
        s = ReplaceString(s, "–","ー");
        s = ReplaceString(s, "—","ー");
        s = ReplaceString(s, "─","ー");
        s = ReplaceString(s, "━","ー");
        s = ReplaceString(s, "―","ー");
        s = ReplaceString(s, "ー","ー");
        s = ReplaceString(s, "ｰ","ー");
        return s;
    }

    /**
     * 漢字カナ姓名のカタカナひらがな部分を正規化し，正規化したカタカナに変換する
     * normalize_basicに追加し
     * 小書き文字を大書文字へ変換
     * 全銀協で使用できない文字を変換
     * @param str 漢字カナ姓名
     * @return 正規化した姓名
     */
    public static String normalize(String str){
        String s = new String(str);
        s = normalize_basic(s);
        s = norm_kogaki(s);
        s = norm_zengin(s);
        return s;
    }

    /**
     * 漢字カナ姓名のカタカナひらがな部分を正規化し，正規化したカタカナに変換する
     * 半角カタカナを全角カタカナへ変換
     * ひらがなを全角カタカナへ変換
     * ２文字に分かれた半濁点，濁点を一文字へ変換
     * 長音に似た文字を「ー」へ変換
     * 半角スペースを全角スペースへ変換
     * @param str　漢字カナ姓名
     * @return 正規化した姓名
     */
    public static String normalize_basic(String str){
        String s = new String(str);
        s = s.toUpperCase();
        // https://qiita.com/parapore/items/1a63c0cd09fea28fd69f
        s = DefaultFullHalf.INSTANCE.toFullwidth(s); // 半角文字を全角文字へ変換
        s = norm_hirakata(s);
        s = norm_dakuten(s);
        s = norm_chouon(s);
        s = ReplaceString(s, " ","　");
        return s;
    }

}
