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

package jp.go.digital.kanjikana.core.engine.foreigner.lib;

import java.util.Arrays;

/**
 * 絣式ローマ字 (v2.0) の定義　<br />
 * <p>
 * (1) フラグ(第3引数)がfalseであるものは、非正規形式 - 正規化により修正される　<br />
 * <p>
 * 母音行は、def_romaji_listで定義する　<br />
 */
public final class Romaji extends RomajiApi {


    {

        def_romaji_list(Arrays.asList(new String[]{"ア", "イ", "ウ", "エ", "オ"}), Arrays.asList("A", "I", "U", "E", "O"), true);

        def_romaji_list(Arrays.asList("ヲ"), Arrays.asList("O"), false);
        def_romaji_list(Arrays.asList(new String[]{"ァ", "ィ", "ゥ", "ェ", "ォ"}), Arrays.asList("a", "i", "u", "e", "o"), false);

        // # 子音行は、def_romaji_lineで定義する
        def_romaji_line(Arrays.asList("ヤ", "-", "ユ", "イェ", "ヨ"), "J", true);
        def_romaji_line(Arrays.asList("ャ", "-", "ュ", "-", "ョ"), "j", false);

        def_romaji_line(Arrays.asList("ワ", "ウィ", "-", "ウェ", "ウォ"), "W", true);
        def_romaji_list(Arrays.asList("ヮ"), Arrays.asList("wa"), false);
        // # def_romaji_list (Arrays.asList(new String[]{ウァ), (Arrays.asList(new String[]{Wa), false

        def_romaji_line(Arrays.asList("カ", "キ", "ク", "ケ", "コ"), "K", true);
        def_romaji_line(Arrays.asList("キャ", "-", "キュ", "キェ", "キョ"), "Kj", true);
        def_romaji_line(Arrays.asList("クァ", "クィ", "-", "クェ", "クォ"), "Kw", true);

        def_romaji_line(Arrays.asList("ガ", "ギ", "グ", "ゲ", "ゴ"), "G", true);
        def_romaji_line(Arrays.asList("ギャ", "-", "ギュ", "ギェ", "ギョ"), "Gj", true);
        def_romaji_line(Arrays.asList("グァ", "グィ", "-", "グェ", "グォ"), "Gw", true);

        // # サ行は要注意
        def_romaji_line(Arrays.asList("サ", "スィ", "ス", "セ", "ソ"), "S", true);
        def_romaji_line(Arrays.asList("シャ", "シ", "シュ", "シェ", "ショ"), "1", true);
        def_romaji_line(Arrays.asList("ザ", "ズィ", "ズ", "ゼ", "ゾ"), "Z", true);
        def_romaji_list(Arrays.asList("ヅィ", "ヅ"), Arrays.asList("Zi", "Zu"), false);

        def_romaji_line(Arrays.asList("ジャ", "ジ", "ジュ", "ジェ", "ジョ"), "3", true);
        def_romaji_line(Arrays.asList("ヂャ", "ヂ", "ヂュ", "ヂェ", "ヂョ"), "3", false);

        def_romaji_line(Arrays.asList("タ", "ティ", "トゥ", "テ", "ト"), "T", true);
        def_romaji_list(Arrays.asList("テュ"), Arrays.asList("Tju"), true);
        def_romaji_list(Arrays.asList("トュ"), Arrays.asList("Tju"), false);

        def_romaji_line(Arrays.asList("チャ", "チ", "チュ", "チェ", "チョ"), "C", true);
        def_romaji_line(Arrays.asList("ツァ", "ツィ", "ツ", "ツェ", "ツォ"), "2", true);

        def_romaji_line(Arrays.asList("ダ", "ディ", "ドゥ", "デ", "ド"), "D", true);
        def_romaji_list(Arrays.asList("デュ"), Arrays.asList("Dju"), true);
        def_romaji_list(Arrays.asList("ドュ"), Arrays.asList("Dju"), false);

        def_romaji_line(Arrays.asList("ハ", "ヒ", "-", "ヘ", "ホ"), "H", true);
        def_romaji_line(Arrays.asList("ヒャ", "-", "ヒュ", "ヒェ", "ヒョ"), "Hj", true);
        def_romaji_line(Arrays.asList("フャ", "-", "フュ", "-", "フョ"), "Fj", true);
        def_romaji_line(Arrays.asList("ファ", "フィ", "フ", "フェ", "フォ"), "F", true);

        def_romaji_line(Arrays.asList("ヴァ", "ヴィ", "ヴ", "ヴェ", "ヴォ"), "V", true);
        def_romaji_line(Arrays.asList("ヴャ", "-", "ヴュ", "-", "ヴョ"), "Vj", true);
        def_romaji_line(Arrays.asList("ビャ", "-", "ビュ", "ビェ", "ビョ"), "Bj", true);
        def_romaji_line(Arrays.asList("バ", "ビ", "ブ", "ベ", "ボ"), "B", true);

        def_romaji_line(Arrays.asList("パ", "ピ", "プ", "ペ", "ポ"), "P", true);
        def_romaji_line(Arrays.asList("ピャ", "-", "ピュ", "ピェ", "ピョ"), "Pj", true);

        def_romaji_line(Arrays.asList("マ", "ミ", "ム", "メ", "モ"), "M", true);
        def_romaji_line(Arrays.asList("ミャ", "-", "ミュ", "ミェ", "ミョ"), "Mj", true);

        def_romaji_line(Arrays.asList("ラ", "リ", "ル", "レ", "ロ"), "R", true);
        def_romaji_line(Arrays.asList("リャ", "-", "リュ", "リェ", "リョ"), "Rj", true);

        def_romaji_line(Arrays.asList("ナ", "ニ", "ヌ", "ネ", "ノ"), "N", true);
        def_romaji_line(Arrays.asList("ニャ", "-", "ニュ", "ニェ", "ニョ"), "Nj", true);

        def_romaji_list(Arrays.asList("ン"), Arrays.asList("0"), true);
        def_romaji_list(Arrays.asList("ッ"), Arrays.asList("!"), true);

        // # 記号
        def_romaji_list(Arrays.asList("ー"), Arrays.asList("+"), true);  //# 長音記号
        def_romaji_list(Arrays.asList("・"), Arrays.asList("@"), true);   //# 中黒
        // # def_romaji_list (Arrays.asList(new String[]{＝), (Arrays.asList(new String[]{=), true   # 等号
        // # def_romaji_list (Arrays.asList(new String[]{－), (Arrays.asList(new String[]{-), true   #

    }
}
