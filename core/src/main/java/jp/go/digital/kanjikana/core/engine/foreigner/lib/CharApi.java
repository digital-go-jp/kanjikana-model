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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CharApi {
    /**
     * 英語表記で使用する文字のテーブル
     */
    protected final Map<String, Type> en_char = new HashMap<String, Type>();
    /**
     * ローマ字表記で使用する文字の定義
     */
    protected final Map<String, CharApi.Type> ja_char = new HashMap<String, CharApi.Type>();
    ;

    enum Type {
        c("c"),
        v("v"),
        C("C"),
        x("x"),
        X("X"),
        V("V"),
        s("s"),
        l("l"),
        Q("O");


        private final String val;

        Type(String val) {
            this.val = val;
        }

        public String getVal() {
            return this.val;
        }

        /**
         * 値に合致する enum 定数を返す。
         */
        public static CharApi.Type getType(String s) {
            // 値から enum 定数を特定して返す処理
            for (CharApi.Type value : CharApi.Type.values()) {
                if (value.getVal().equals(s)) {
                    return value;
                }
            }
            return null; // 特定できない場合
        }
    }

    /**
     * 文字のタイプを定義する
     *
     * @param table
     * @param list
     * @param type
     */
    void define_char(Map<String, CharApi.Type> table, List<String> list, CharApi.Type type) {
        for (String s : list) {
            table.put(s, type);
        }
    }

    /**
     * [API] valid_en_string? (規範的なアルファベット文字列)
     *
     * @param s
     * @return
     */
    boolean valid_en_string(String s) {
        for (char c : s.toLowerCase().toCharArray()) {
            String ss = String.valueOf(c);
            if (!this.en_char.containsKey(ss)) {
                return false;
            }
        }
        return true;
    }

    /***
     * [API] valid_ja_string? (カタカナ文字列 - これだけでは規範的であるかどうかは確定しない)
     * @param s
     * @return
     */
    boolean valid_ja_string(String s) {
        return s.matches("^[ァ-ヴー＝－・]+$");
    }

    /**
     * [API] type_string (タイプ文字列 = 文字のタイプの列)
     *
     * @param lst ["ア","リ","ス"]
     * @return
     */
    public String en_type_string(List<String> lst) {
        return type_string(en_char, lst);
    }

    public String ja_type_string(List<String> lst) {
        return type_string(ja_char, lst);
    }

    public String type_string(Map<String, CharApi.Type> table, List<String> lst) {
        List<String> res = new ArrayList<>();
        for (String s : lst) {
            String ss = "";
            if (table.containsKey(s)) {
                ss = table.get(s).getVal();
            } else if (table.containsKey(s.toUpperCase())) {
                ss = table.get(s.toUpperCase()).getVal();
            }
            res.add(ss);
        }
        return String.join("", res);
    }

    /**
     * 日本語の子音
     *
     * @param s
     * @return
     */
    boolean ja_consonant(String s) {
        return this.ja_char.get(s) == CharApi.Type.C;
    }

    /**
     * # 日本語の文字は、次の４タイプ
     * # (1) Vl?        (母音字, 母音字+長音記号)
     * # (2) Cs?vl?     (子音字+母音字(+長音記号), 子音字+半母音字+母音字(+長音記号))
     * # (3) 0          (ン)
     * # (4) Q          (ッ)
     * #
     * # Csタイプは
     * # [j] kj, gj, hj, fj, vj, bj, pj, mj, rj, nj
     * # [w] kw, gw
     * #
     * # (1)(2)をバラすと
     * # (1.1) V         |V
     * # (1.2) VL        |Vl
     * # (2.1) Cv       C|v
     * # (2.2) Cvl      C|vl
     * # (2.3) Csv     Cs|v
     * # (2.4) Csvl    Cs|vl
     */

    //  属性作成 (対応関係(corr)を属性リストに変換する) --- MeCab学習コーパス作成時に必要
    private static final String EmptyChar = "*";

    private List<List<String>> corr_to_feature(List<String> corr) {
        String ka = corr.get(0);
        String en = corr.get(1);
        String ro = corr.get(2);
        List<List<String>> res = new ArrayList<>();
        if (ka.equals("")) { // 英語側の消失
            res.add(Arrays.asList(new String[]{en}));
            List<List<String>> v = corr_to_feature_delete(ka, ro, en);
            res.addAll(v);
        } else {
            res.add(Arrays.asList(new String[]{en}));
            List<List<String>> v = corr_to_feature_normal(ka, ro, en);
            res.addAll(v);
        }
        return res;
    }


    /**
     * 属性: 9属性 (0-1: アルファベット側, 2: カタカナ, 3-8: ローマ字側) <br/>
     * 0: アルファベット文字列(en) <br/>
     * 1: アルファベットタイプ文字列(en_type_string) <br/>
     * 2: カタカナ文字列(ka) <br/>
     * 3: ローマ字タイプ文字列(ja_type_string) <br/>
     * 4: 末尾の母音字列 <br/>
     * 5: 末尾の母音字 <br/>
     * 6: 母音タイプ：V(通常), L(長音), Q(促音) <br/>
     * 7: 先頭の子音字列(半母音も含む) <br/>
     * 8: 先頭の子音字 <br/>
     *
     * @param ka
     * @param ro
     * @param en
     * @return
     */

    private List<List<String>> corr_to_feature_delete(String ka, String ro, String en) {
        String lst = this.en_type_string(new ArrayList<String>(Arrays.asList(en.split(""))));
        List<List<String>> res = new ArrayList<>();
        res.add(Arrays.asList(new String[]{en}));
        res.add(Arrays.asList(new String[]{lst}));

        List<String> list = Stream.generate(() -> EmptyChar).limit(7).collect(Collectors.toList());
        res.add(list);
        return res;
    }

    private List<List<String>> corr_to_feature_normal(String ka, String ro, String en) {
        List<List<String>> res = new ArrayList<>();
        res.add(Arrays.asList(en));
        res.add(Arrays.asList(en_type_string(Arrays.asList(en.split("")))));

        res.add(Arrays.asList(ka));
        res.add(Arrays.asList(ja_type_string(Arrays.asList(ro.split("")))));

        res.add(ja_consonant_feature(ro));
        res.add(ja_vowel_feature(ro));

        return res;
    }


    /**
     * 末尾の母音に関する属性：３属性
     *
     * @param ro
     * @return
     */
    private List<String> ja_vowel_feature(String ro) {
        List<String> string = new ArrayList<>();
        String tail = null;
        String type = null;
        List<String> lst = Arrays.asList(ro.split(""));
        Collections.reverse(lst);
        for (String c : lst) {
            if (!Pattern.compile("[vVlQ]").matcher(ja_char.get(c).getVal()).find()) {
                break;
            }
            string.add(c);
            if (tail == null && Pattern.compile("[vV]").matcher(ja_char.get(c).getVal()).find()) {
                tail = c; //末尾の母音字
            }
            if (type == null) {
                type = ja_char.get(c).getVal();
            }
        }
        List<String> out = new ArrayList<>();
        if (string.isEmpty()) {//# 末尾の母音字列
            out.add(EmptyChar);
        } else {
            Collections.reverse(string);
            out.add(String.join("", string));
        }
        if (tail != null) { //# 末尾の母音字
            out.add(tail);
        } else {
            out.add(EmptyChar);
        }
        if (type != null) {//# 母音タイプ：V(通常), L(長音), Q(促音)
            out.add(type);
        } else {
            out.add(EmptyChar);
        }
        return out;
    }

    /**
     * 先頭の子音に関する属性：２属性
     *
     * @param ro
     * @return
     */
    private List<String> ja_consonant_feature(String ro) {
        List<String> lst = new ArrayList<>();
        String head = null;
        for (String c : ro.split("")) {
            if (ja_char.containsKey(c) && !Pattern.compile("[Cs]").matcher(ja_char.get(c).getVal()).find()) {
                break;
            }
            lst.add(c); //# 子音字, 半母音字
            if (head == null && Pattern.compile("[C]").matcher(ja_char.get(c).getVal()).find()) {
                // # 先頭の子音字
                head = c;
            }
        }
        List<String> out = new ArrayList<>();
        if (lst.isEmpty()) {
            out.add(EmptyChar);
        } else {
            out.add(String.join("", lst));
        }
        if (head != null) {
            out.add(head);
        } else {
            out.add(EmptyChar);
        }
        return out;
    }
}
