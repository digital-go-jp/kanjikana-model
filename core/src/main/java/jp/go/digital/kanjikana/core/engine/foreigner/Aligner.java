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

import jp.go.digital.kanjikana.core.engine.foreigner.aligner.Dp;
import jp.go.digital.kanjikana.core.engine.foreigner.aligner.DpList;
import jp.go.digital.kanjikana.core.engine.foreigner.aligner.DpResult;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * カタカナ-ローマ字 / 英語アルファベット表記のアライメントをとる <br />
 * 処理の概要 <br />
 * (Step 1) カタカナ文字列から、カタカナ-ローマ字対応リストを作成する Romaji.k2r <br />
 * (Step 2) ローマ字文字列と英語文字列から、ローマ字-英語対応リストを作成する dp (alinger/dp.rb) <br />
 * (Step 3) ローマ字-英語対応リストを、カタカナ単位の対応リストに変換する _kunit_pack <br />
 * (Step 4) カタカナ-ローマ字対応リストと(Step 3)のカタカナ単位の対応リストから、 <br />
 * カタカナ-ローマ字-英語対応リストを作る _insert_katakana <br />
 * <p>
 * [ToDo] <br />
 * - メソッドの名前を統一感のあるものに変更する <br />
 */
public final class Aligner extends Dp {

    //private final Map<String, Integer> table = new HashMap<>();
    public Aligner() throws Exception {
        super();

    }

    /**
     * 部分対応のスコア(自動設定)
     *
     * @param ja
     * @param en
     * @return
     */
    protected int _default_score(String ja, String en) {

        int val1 = _default_score_sub(charApi.ja_type_string(Arrays.asList(ja.split(""))), ja);
        int val2 = _default_score_sub(_default_score_en_adjust(charApi.en_type_string(Arrays.asList(en.split(""))), _default_score_head(ja)), en);
        return val1 + val2;
    }

    /**
     * ローマ字の先頭のtype文字 (空の場合はnil）
     *
     * @param ja
     * @return
     */
    private String _default_score_head(String ja) {
        if (ja.length() == 0) {
            return "";
        }
        String str = charApi.ja_type_string(Arrays.asList(ja.split("")));
        if (str.length() == 0) {
            return "";
        }
        return str.substring(0, 1);
    }

    /**
     * スコアの計算法
     *
     * @param t_string
     * @param string
     * @return
     */
    private int _default_score_sub(String t_string, String string) {
        if (t_string.equals("")) {
            return 0;
        } else {
            String[] list = t_string.split("");
            int l = list.length;
            int vals = 0;
            for (String c : list) {
                vals += _default_score_type_char(c);
            }
            return vals - l * l;
        }
    }

    /**
     * 英語側の補正: 子音字が母音として働いていると思われる場合
     *
     * @param t_string
     * @param head
     * @return
     */
    private String _default_score_en_adjust(String t_string, String head) {
        if (head.matches("^[Vvs]$") && !head.matches("C")) { // ja側は母音(Vv)か半母音(s)始まりで、子音(C)を含まない
            // En側の子音のスコアを、母音のスコアと同じにする
            return t_string.replaceAll("c", "v");
        } else {
            return t_string;
        }
    }

    /**
     * 字のスコア
     *
     * @param c
     * @return
     */
    private int _default_score_type_char(String c) {
        if (c.equals("v") || c.equals("l")) {
            return 500; //   # 母音字, 長音記号,
        } else if (c.equals("s")) {
            return 750; //# 半母音字(Ja, (En))
        } else if (c.equals("C") || c.equals("c") || c.equals("V")) {
            return 1000;  //  # 子音字(Ja), 子音字(En), 母音字=ア行(Ja)
        } else if (c.equals("Q") || c.equals("X")) {
            return 2000;  // # 促音(Ja), 記号
        }
        return 0;
    }


    /** API **/


    /**
     * @param ka     [["デ", "De"], ["ニ", "Ni"], ["ス", "Su"], ["・", "@"], ["アー", "A+"], ["バー", "Ba+"], ["グ", "Gu"]]
     * @param ro     "DeNiSu@A+Ba+Gu"
     * @param en     "DeNiSu@A+Ba+Gu"
     * @param params {:check=>:standard, :raw=>false}
     * @return
     */
    public ResultAwase awase(List<List<String>> ka, String ro, String en, ParamsForeigner params) throws Exception {
        DpResult r = dp_main(ro, en);
        List<List<String>> ra = _kunit_pack(r.getList());
        List<List<String>> out = _insert_katakana(ra, ka); // [["デ", "De", "de"], ["ニ", "Ni", "nni"],
        ResultAwase resultAwase = new ResultAwase();
        resultAwase.doubtful = doubtful(out, r.getList(), params);
        resultAwase.r = r.getList();
        resultAwase.out = out;
        return resultAwase;
    }

    /**
     * ローマ字-英語アライメントを、カタカナ単位にまとめる
     * （注）挿入と脱落について、よく考える必要がある
     *
     * @param list
     */
    private List<List<String>> _kunit_pack(List<DpList> list) {
        List<List<String>> out = new ArrayList<>(); // [[ja,en],[ja,en],..
        String ja = "";
        String en = "";
        for (int i = 0; i < list.size(); i++) {
            int s = 0;
            List<String> j, e;
            DpList dp = list.get(i);
            s = dp.getScore();
            j = dp.getSource();
            e = dp.getTarget();
            ja += String.join("", j);
            en += String.join("", e);

            if (i + 1 < list.size() && !list.get(i + 1).getSource().isEmpty() && list.get(i + 1).getSource().get(0).matches("^[+!]$")) { // 次の要素は 長音記号('+') or 促音('!')
                continue; // 繋げる
            } else if (i + 1 < list.size()  // 次の要素が存在
                    && list.get(i + 1).getSource().size() == 1 && list.get(i + 1).getSource().get(0).matches(".*[aiueo0]$") // 次の要素は小書母音字 or 撥音('0')
                    && list.get(i + 1).getTarget().isEmpty()) { // 英語側は存在しない
                continue; // 繋げる
            } else if (ja.length() == 0 && en.length() != 0) { // 英語側のみ存在する（カタカナ文字間）
                out.add(Arrays.asList(ja, en));
                ja = "";
                en = "";
            } else if (ja.matches(".*[@=\\-|]$")  // かならず区切る単位: 中黒('@'), ＝('='), ハイフォン('－')
                    || ja.matches(".*[AIUEOaiueo\\+0\\!]$")) { // カタカナ文字の末尾: 母音字, 長音記号('+'), 撥音, 促音
                out.add(Arrays.asList(ja, en));
                ja = "";
                en = "";
            }
        }
        if (ja.length() != 0 || en.length() != 0) { // 最後
            out.add(Arrays.asList(ja, en));
        }
        return out;
    }

    private List<List<String>> dup(List<List<String>> list) {
        List<List<String>> out = new ArrayList<>();
        for (List<String> l : list) {
            out.add(new ArrayList<>(l));
        }
        return out;
    }

    /**
     * ローマ字(カタカナ単位)-アルファベット対応に、カタカナ文字を追加する！
     *
     * @param r2a [ [ローマ字, アルファベット], ... ]    e.g., [["E", "ai"], ["Ba", "bou"], ["+", "t"]]
     * @param k2r [ [カタカナ, ローマ字], ... ]          e.g., [["エ", "E"], ["バー", "Ba+"]]
     * @return [ [カタカナ, ローマ字, アルファベット], ... ]  e.g., [["エ", "E", "ai"], ["バー", "Ba+", "bout"]]
     */
    private List<List<String>> _insert_katakana(List<List<String>> r2a, List<List<String>> k2r) throws Exception {
        List<List<String>> out = new ArrayList<>(); // [[ja,en],[ja,en],..
        r2a = dup(r2a);
        k2r = dup(k2r);
        String ra = "";
        String aa = "";
        String kc = "";
        String rc = "";

        boolean boundary = true; // カタカナ文字境界

        while (!r2a.isEmpty() && !k2r.isEmpty()) { // 両方とも存在する
            // pp [ [kc, rc, ra, aa ], k2r, r2a, out ]
            if (boundary) { // カタカナ文字境界にいる
                if (r2a.get(0).get(0).isEmpty()) {  // アルファベットの湧き出し(ローマ字は空なので、対応するカタカナはない)
                    List<String> p = r2a.remove(0);
                    out.add(Arrays.asList("", p.get(0), p.get(1)));
                    continue;
                } else {
                    List<String> p = r2a.remove(0);
                    ra = p.get(0);
                    aa = p.get(1);
                    List<String> q = k2r.remove(0); //  両側を補充
                    kc = q.get(0);
                    rc = q.get(1);
                }
            } else if (ra.length() < rc.length()) { // アルファベット側が足りない
                ra += r2a.get(0).get(0);  // アルファベット側を補充
                aa += r2a.get(0).get(1);
                r2a.remove(0);

            } else if (ra.length() > rc.length()) {
                kc += k2r.get(0).get(0); // カタカナ側を補充
                rc += k2r.get(0).get(1);
                k2r.remove(0);
            }

            if (ra.length() == rc.length()) { // 対応が取れた
                out.add(Arrays.asList(kc, rc, aa));
                ra = "";
                aa = "";
                kc = "";
                rc = "";
                boundary = true; // カタカナ文字境界
            } else {
                boundary = false;
            }
        }

        // 片側だけ存在する
        if (!r2a.isEmpty()) { // 対応のみが残っている (カタカナは使い切った!)
            while (!r2a.isEmpty()) {
                // pp [:last1, [kc, rc, ra, aa ], k2r, r2a, out, boundary, ra.length > rc.length ]
                if (boundary) { // カタカナ文字境界にいる
                    for (List<String> r : r2a) {
                        out.add(Arrays.asList("", r.get(0), r.get(1)));// すべてアルファベット側の湧き出し
                    }
                    //out.add(Arrays.asList("",r2a.get(0).get(0),r2a.get(0).get(1))); // すべてアルファベット側の湧き出し
                    break;
                } else if (ra.length() < rc.length()) {
                    ra += r2a.get(0).get(0); // アルファベット側を補充
                    aa += r2a.get(0).get(1);
                    r2a.remove(0); // shift
                }

                if (ra.length() == rc.length()) {
                    out.add(Arrays.asList(kc, rc, aa));
                    ra = "";
                    aa = "";
                    kc = "";
                    rc = "";
                    boundary = true; // カタカナ文字境界
                } else if (ra.length() > rc.length()) {
                    // ここに来ることはないはず
                    throw new Exception("something wrong ");
                }

            }
        } else if (!k2r.isEmpty()) { // カタカナ側が残っている
            while (!k2r.isEmpty()) {
                // pp [:last2, [kc, rc, ra, aa ], k2r, r2a, out, boundary, ra.length > rc.length ]
                if (boundary) {  // カタカナ文字境界にいる
                    // すべて、カタカナの湧き出し
                    StringBuilder sb0 = new StringBuilder();
                    k2r.forEach(s -> sb0.append(s.get(0)));
                    StringBuilder sb1 = new StringBuilder();
                    k2r.forEach(s -> sb1.append(s.get(1)));
                    out.add(Arrays.asList(sb0.toString(), sb1.toString(), ""));
                    break;
                } else if (ra.length() > rc.length()) {
                    kc += k2r.get(0).get(0);
                    rc += k2r.get(0).get(1);
                    k2r.remove(0); // shift
                }

                if (ra.length() == rc.length()) {
                    out.add(Arrays.asList(kc, rc, aa));
                    ra = "";
                    aa = "";
                    kc = "";
                    rc = "";
                    boundary = true;  // カタカナ文字境界
                } else if (ra.length() < rc.length()) {
                    // ここに来ることはないはず
                    throw new Exception("somethig wrong ");
                }
            }
        }

        return out;
    }


    /**
     * 疑わしきアライメント
     *
     * @param align  [["デ", "De", "de"], ["ニ", "Ni", "nni"], ["ス", "Su", "s"],
     * @param detail [[1998, ["D"], ["d"]], [998, ["e"], ["e"]],
     * @param params {:check=>:standard, :raw=>false}
     * @return
     */
    private boolean doubtful(List<List<String>> align, List<DpList> detail, ParamsForeigner params) {
        if (params.getCheck().equals(ParamsForeigner.Check.strict)) {
            return doubtful_standard(detail, true);
        } else if (params.getCheck().equals(ParamsForeigner.Check.loose)) {
            return doubtful_loose(align);
        } else {
            return doubtful_standard(detail, false);
        }
    }


    /**
     * 標準的なチェック
     *
     * @param detail
     * @param strict
     * @return
     */
    private boolean doubtful_standard(List<DpList> detail, boolean strict) {
        boolean flag = false;
        int l = detail.size();
        boolean tail = true;
        List<DpList> rev = new ArrayList<>(detail); // dup
        Collections.reverse(rev);
        for (int i = 0; i < rev.size(); i++) {
            DpList x = rev.get(i);
            int score = x.getScore();
            List<String> ja = x.getSource();
            List<String> en = x.getTarget();
            if (score <= 100) { // 辞書にない対応
                String jas = String.join("", ja);
                String ens = String.join("", en);
                if (ens.isEmpty()) {
                    if (!acceptable_jas_missmatch(jas, rev, i)) {
                        return true;
                    }
                } else if (jas.isEmpty()) {
                    if (strict) {
                        if (!(tail && extra_en_consonant(ens))) {  // アルファベットの末尾は子音(読まない)
                            return true;
                        }
                    } else {
                        if (ens.equals("@")) {
                            // 英語側の全角スペース(区切り記号)は、日本語側では消失してもよい


                        } else {
                            if (!(tail && extra_en_consonant(ens))) { // アルファベットの末尾は子音(読まない)
                                return true;
                            }
                        }
                    }

                } else if (charApi.ja_type_string(Arrays.asList(jas.split(""))).matches("^[Cs]+$")
                        && charApi.en_type_string(Arrays.asList(ens.split(""))).matches("^[c]+$")) { // 両方が子音字
                    if (strict) {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                tail = false;
            }
        }
        return flag;
    }

    /**
     * 許容できる日本語側のミスマッチ
     *
     * @param jas
     * @param rev
     * @param i
     * @return
     */
    private boolean acceptable_jas_missmatch(String jas, List<DpList> rev, int i) {
        return jas.equals("!") ||  // 促音「ッ」の湧き出し
                jas.equals("+") ||  //長音記号「ー」の湧き出し
                !jas.isEmpty() && extra_ja_vowel(jas, rev, i + 1); // 子音直後の母音の湧き出し e.g.「t/ト」
    }


    private boolean extra_ja_vowel(String jas, List<DpList> rev, int i) {
        Pattern p = Pattern.compile("^[aiueo][\\+]?$", Pattern.CASE_INSENSITIVE);
        return extra_ja_vowel_sub(rev, i) || // 直前の日本語側の末尾が子音
                p.matcher(jas).matches(); // 湧き出しているのが母音
    }

    private boolean extra_ja_vowel_sub(List<DpList> rev, int i) {
        Pattern p = Pattern.compile("^.*[aiueo\\+\\!]$", Pattern.CASE_INSENSITIVE);
        while (i < rev.size()) {
            int score = rev.get(i).getScore();
            List<String> ja = rev.get(i).getSource();
            List<String> en = rev.get(i).getTarget();

            if (ja.isEmpty()) { // 日本語側が空なので、もう一つ前を見に行く
                i++;
            } else if (!p.matcher(String.join("", ja)).matches()) { // 末尾が子音
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 子音
     *
     * @param ens
     * @return
     */
    private boolean extra_en_consonant(String ens) {
        Pattern p = Pattern.compile("[aiueo]", Pattern.CASE_INSENSITIVE);
        return !p.matcher(ens).find();
    }

    private boolean doubtful_loose(List<List<String>> align) {
        return align.isEmpty() || // アライメントが空
                align.stream().filter(x -> x.size() > 1 && x.get(2).isEmpty()).findAny().isPresent(); // カタカナの湧き出しが存在する
    }
}
