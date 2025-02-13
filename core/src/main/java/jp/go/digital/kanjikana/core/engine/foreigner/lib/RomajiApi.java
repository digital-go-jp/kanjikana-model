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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class RomajiApi {

    /**
     * 変換テーブル { カタカナ: ローマ字, ...,
     * ローマ字: カタカナ, ... }
     */
    private final Map<String, String> table = new HashMap<String, String>();


    private List<String> calc_1moji(String key) {
        List<String> res = new ArrayList<>();
        res.add(key);

        if (this.table.containsKey(key)) {
            res.add(this.table.get(key));
        } else {
            res.add(key);
        }
        return res;
    }

    /**
     * ローマ字を定義する
     *
     * @param kana
     * @param romaji
     * @param flag   逆変換を定義するかどうか
     */
    private void def_romaji(String kana, String romaji, boolean flag) {
        if (!this.table.containsKey(kana)) {
            this.table.put(kana, romaji);  //  # 順方向変換(カタカナ->ローマ字)を定義する
        }
        if (flag && !this.table.containsKey(romaji)) {
            this.table.put(romaji, kana);  // # 逆変換変換(ローマ字->カタカナ)を定義する
        }
    }

    /**
     * 複数のローマ字を一度に定義する
     *
     * @param klist
     * @param rlist
     * @param flag
     */
    void def_romaji_list(List<String> klist, List<String> rlist, boolean flag) {
        int min_size = Math.min(klist.size(), rlist.size());
        for (int i = 0; i < min_size; i++) {
            def_romaji(klist.get(i), rlist.get(i), flag);
        }
    }

    /**
     * 行全体に対してローマ字を定義する
     * '-' は定義しないことを意味する
     *
     * @param klist
     * @param c     子音字
     * @param flag  逆方向を定義するか
     */
    void def_romaji_line(List<String> klist, String c, boolean flag) {
        List<String> v = Arrays.asList(new String[]{"a", "i", "u", "e", "o"});
        for (int i = 0; i < 5; i++) {
            if (!klist.get(i).equals("-")) {
                def_romaji(klist.get(i), c + v.get(i), flag);
            }
        }
    }

    /**
     * [API] カタカナ-ローマ字変換
     * 対応関係を出力する
     *
     * @param s
     * @return
     */
    public List<List<String>> k2r(String s, boolean align) throws Exception {
        if (!align) {
            throw new Exception("invalid argument");
        }
        return k2r_sub(Arrays.asList(s.split("")), true);
    }

    /**
     * [API] カタカナ-ローマ字変換
     *
     * @param string
     * @return
     */
    public String k2r(String string) throws Exception {
        List<List<String>> out = k2r_sub(Arrays.asList(string.split("")), true);
        StringBuilder sb = new StringBuilder();
        for (List<String> s : out) {
            sb.append(s.get(1));
        }
        return sb.toString();
    }


    /**
     * 対応関係を出力する
     *
     * @param list  ["デ","ニ","ス"]
     * @param align always true
     * @return
     */
    private List<List<String>> k2r_sub(List<String> list, boolean align) throws Exception {
        if (!align) {
            throw new Exception("invalid argument");
        }
        List<List<String>> out = _k2r_sub(list);
        // アライメント(::Array)を返すか、あるいは、文字列を返す)
        return k2r_sub2(out);// : String.join("",out.stream().map(x->x.get(1)).collect(Collectors.toList()));
    }

    private String k2r_sub(List<String> list) {
        List<List<String>> out = _k2r_sub(list);
        return String.join("", out.stream().map(x -> x.get(1)).collect(Collectors.toList()));
    }

    private List<List<String>> _k2r_sub(List<String> list) {
        List<List<String>> out = new ArrayList<>();
        int l = list.size();
        int i = 0;
        while (i < l) {
            if (i + 1 < list.size() && !list.get(i + 1).isEmpty()) {   // 2文字単位の変換
                String key = String.join("", list.subList(i, i + 2));
                if (table.containsKey(key)) {
                    String x = table.get(key);
                    out.add(Arrays.asList(key, x));
                    i += 2;
                    continue;
                }
            }
            // 1文字単位の変換
            if (table.containsKey(list.get(i))) {
                out.add(Arrays.asList(list.get(i), table.get(list.get(i))));
            } else {
                out.add(Arrays.asList(list.get(i), list.get(i)));
            }
            i++;

        }
        return out;
    }

    /**
     * 長音符号を接続する(独立した対応とはしない)
     *
     * @param list [["フィ","Fi"], ..
     * @return
     */
    private List<List<String>> k2r_sub2(List<List<String>> list) {
        List<List<String>> out = new ArrayList<>();
        for (List<String> pair : list) {
            String ja = pair.get(0);
            String en = pair.get(1);
            if (ja.equals("ー")) {
                if (out.size() == 0) {
                    return out;// 禁則文字で始まっている
                }
                String oja = out.get(out.size() - 1).get(0);
                String oen = out.get(out.size() - 1).get(1);
                out.get(out.size() - 1).set(0, oja + ja);
                out.get(out.size() - 1).set(1, oen + en);
            } else {
                out.add(pair);
            }
        }
        return out;
    }


    /**
     * [API] ローマ字-カタカナ変換（逆変換）
     * 対応関係を出力する
     *
     * @param string
     * @param align  always true
     * @return
     */
    public List<List<String>> r2k(String string, boolean align) throws Exception {
        if (!align) {
            throw new Exception("invalid argument");
        }
        return r2k_sub(Arrays.asList(string.split("")));
    }

    /**
     * [API] ローマ字-カタカナ変換（逆変換）
     *
     * @param string
     * @return
     */
    public String r2k(String string) {
        List<List<String>> out = r2k_sub(Arrays.asList(string.split("")));
        StringBuilder sb = new StringBuilder();
        for (List<String> s : out) {
            sb.append(s.get(1));
        }
        return sb.toString();
    }

    private List<List<String>> r2k_sub(List<String> list) {
        List<List<String>> out = new ArrayList<>();
        int l = list.size();
        int i = 0;
        while (i < l) {
            for (int n = 3; n > 0; n--) { //  # ローマ字の長さの最大値は3
                String x = this.table.get(String.join("", list.subList(i, n)));
                if (i + n - 1 < list.size() && x != null) {
                    List<String> res = new ArrayList<>();
                    res.add(this.table.get(String.join("", list.subList(i, n))));
                    res.add(list.get(i));
                    out.add(res);
                    i += n;
                } else if (n == 1) {
                    List<String> res = new ArrayList<>();
                    res.add(list.get(i));
                    res.add(list.get(i));
                    out.add(res);
                    i += n;
                }
            }
        }
        return out;
    }


    /**
     * [API] カタカナ正規化 [2017.04.10]
     * 対応関係を出力する
     *
     * @param string
     * @param align  always true
     * @return
     */
    public List<List<String>> k2k(String string, boolean align) throws Exception {
        if (!align) {
            throw new Exception("invalid argument");
        }
        return k2k_sub_aligned(Arrays.asList(string.split("")));
    }

    /**
     * [API] カタカナ正規化 [2017.04.10]
     *
     * @param string
     * @return
     */
    public String k2k(String string) {
        return k2k_sub(Arrays.asList(string.split("")));
    }

    private String k2k_sub(List<String> list) {
        List<List<String>> out = k2k_sub_aligned(list);
        StringBuilder sb = new StringBuilder();
        for (List<String> s : out) {
            sb.append(s.get(1));
        }
        return sb.toString();
    }

    private List<List<String>> k2k_sub_aligned(List<String> list) {
        List<List<String>> out = new ArrayList<>();
        int l = list.size();
        int i = 0;
        while (i < l) {
            List<String> res = new ArrayList<>();
            String x = "";
            //var tmp = String.join("", list.subList(i,i+2));
            if (i + 1 < l) {
                x = this.table.get(String.join("", list.subList(i, i + 2))); // list[i,2]
            } else {
                x = this.table.get(list.get(i));
            }

            if (i + 1 < list.size() && list.get(i + 1) != null && x != null) { //  2文字単位の正規化
                //             # ２文字でローマ字が定義されている
                res.add(String.join("", list.subList(i, i + 2)));
                res.add(this.table.get(x));
                out.add(res);
                i += 2;
            } else {  // 1文字単位の正規化
                Pattern p = Pattern.compile("^[ァィゥェォャュョヮ]$");
                Matcher m = p.matcher(list.get(i));
                if (m.matches()) {

                    // ローマ字が定義されていない小書き文字
                    if (!out.isEmpty() && out.get(out.size() - 1) != null && !out.get(out.size() - 1).isEmpty()) {
                        //List<String> lst = Arrays.asList(this.table.get(out.get(out.size()-1).get(1)));

                        // table[out[-1][1]].split(//)[-1].downcase
                        String s0 = this.table.get(out.get(out.size() - 1).get(1));
                        String[] s1 = s0.split("");
                        String s2 = s1[s1.length - 1].toLowerCase();

                        String[] t0 = this.table.get(list.get(i)).split("");
                        String t1 = t0[0];

                        boolean flg1 = s2.equals(t1);


                        if (flg1 && (i + 1 >= list.size() || !list.get(i + 1).equals("ー"))) {
                            // 同じ母音が連続していて、次が長音記号でなければ、長音記号化する
                            res.add(list.get(i));
                            res.add("ー");
                            out.add(res);
                            i++;
                            continue;
                        }
                    }
                    // そうでなければ、通常の大きさに変換する
                    res.add(list.get(i));
                    res.add(Utils.replaceYouon(list.get(i)));
                    out.add(res);

                } else {
                    //  通常の大きさの文字は、ローマ字が定義されている
                    //  list[i](カタカナ) -> table[list[i](ローマ字) -> table[table[list[i]]](カタカナ)
                    res.add(list.get(i));
                    res.add(this.table.get(this.table.get(list.get(i))));
                    out.add(res);
                }
                i++;
            }
        }
        return out;
    }
}

