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

package jp.go.digital.kanjikana.core.engine.foreigner.aligner;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.foreigner.lib.Char;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 Awase23D::Alinger::DP - DPによるアライメント
 */
public abstract class Dp {
    Logger logger = LogManager.getLogger(Dp.class);

    protected int ja_max;
    protected int en_max;

    protected Char charApi = new Char();

    protected final Map<String, Integer> table = new HashMap<>();

    private final String DefaultFile = Resources.getProperty(Resources.PropKey.FOREIGNER_DIC);

    public Dp() throws Exception {
        ja_max = 0; //                    # ja側の最長の文字列
        en_max = 0; //                    # en側の最長の文字列
        setResource(DefaultFile);
    }


    private void setResource(String path) throws Exception {
        List<String> lines = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        for (String line; (line = br.readLine()) != null; ) {
            lines.add(line);
        }

        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\s*\\#")) { // コメント行をスキップ
                continue;
            }
            line = line.replaceAll("\\s*\\#.*$", ""); // コメントを削除
            if (line.matches("^\\s*$")) { // 空行をスキップ
                continue;
            }
            String ja = "";
            String en = "";
            String score_str = "";
            String[] tmp = line.split("\t");
            int score = 0;
            if (tmp.length == 2) {
                ja = tmp[0];
                en = tmp[1];
            } else if (tmp.length == 3) {
                ja = tmp[0];
                en = tmp[1];
                score_str = tmp[2];
            }
            ja = ja.trim();
            en = en.trim();
            score_str = score_str.trim();
            if (tmp.length == 2 || score_str.equals("*")) { // スコアが明示的に定義されていない
                score = _default_score(ja, en);  // スコアを設定する
            } else if (score_str.matches("^[+\\-].*$")) { // ボーナス or ペナルティが記述されている！
                score = _default_score(ja, en) + Integer.parseInt(score_str);
            } else {
                score = Integer.parseInt(score_str); // スコアが定義されている
            }
            if (table.containsKey(ja + ":" + en)) {
                throw new Exception("Duplicated: " + line);
            }
            table.put(ja + ":" + en, score);
            ja_max = ja_max > ja.length() ? ja_max : ja.length();
            en_max = en_max > en.length() ? en_max : en.length();
        }
    }

    protected abstract int _default_score(String ja, String en);

    /**
     * # Dynamic Programmingによるアライメント
     * #
     * # source: ja - ローマ字文字列
     * # target: en - アルファベット文字列
     *
     * @param source ja - ローマ字文字列
     * @param target en - アルファベット文字列
     * @return
     */
    public DpResult dp_main(String source, String target) {
        return dp_main(Arrays.asList(source.split("")), Arrays.asList(target.split("")));
    }

    /**
     * # DP本体
     * #    dptの要素: [score, list]
     * #      listの要素: [score, s, t]
     * #
     * # (注意)
     * #   スコアは、与えられたペアに対して、ベストの部分対応を見つけるためのものである
     * #   最終的に得られるスコアの値は、意味を持たない
     * #   (スコアが高いことは、正しい対訳ペアであることを意味しない)
     *
     * @param source
     * @param target
     * @return
     */
    public DpResult dp_main(List<String> source, List<String> target) {
        List<List<DpResult>> dpt = new ArrayList<>();
        for (int i = 0; i <= source.size(); i++) {
            dpt.add(new ArrayList<>());
            for (int j = 0; j <= target.size(); j++) {
                if (i == 0 && j == 0) {
                    dpt.get(i).add(new DpResult());
                } else {
                    dpt.get(i).add(dp_sub(dpt, i, j, source, target));
                }
            }
        }
        return dpt.get(source.size()).get(target.size());
    }

    private DpResult dp_sub(List<List<DpResult>> dpt, int i, int j, List<String> source, List<String> target) {
        List<DpResult> cand = new ArrayList<>();
        // 可能な対応をトライする (@ja_max, @en_maxを参照する)
        for (int p = 0; p <= Math.min(i, ja_max); p++) {
            List<String> s = source.subList(i - p, i);
            for (int q = 0; q <= Math.min(j, en_max); q++) {
                List<String> t = target.subList(j - q, j);
                if (p == 0 && q == 0) { // empty-emptyは作らない
                    continue;
                }
                // s: 長さp; t: 長さ9
                int score = dp_c_score(s, t);
                DpResult e = dpt.get(i - p).get(j - q); // 接続させるポイント
                cand.add(new DpResult(e.getScore() + score, dp_sub_condense(e.getList(), score, s, t)));
            }
        }
        List<DpResult> sorted_cand = cand.stream().sorted((a, b) -> b.getScore() - a.getScore()).collect(Collectors.toList());  // 降順
        return sorted_cand.get(0);
    }

    private List<DpList> dp_sub_condense(List<DpList> list, int score, List<String> s, List<String> t) {
        List<DpList> res = new ArrayList<>();
        if (score == 0 && s.isEmpty() && list.size() > 0 && list.get(list.size() - 1).getScore() == 0 && list.get(list.size() - 1).getSource().isEmpty()) {
            // 不要な対応を作らない -> 末尾にマージする
            List<DpList> slist = list.subList(0, list.size() - 1); // 末尾削除
            res.addAll(slist);
            List<String> lastList = new ArrayList<>();
            for (String ss : list.get(list.size() - 1).getTarget()) {
                lastList.add(ss);
            }
            //var lastList = list.get(list.size()-1).getTarget();
            for (String tt : t) {
                lastList.add(tt);
            }
            //lastList.addAll(t);
            res.add(new DpList(0, s, lastList));
        } else {
            res.addAll(list);
            res.add(new DpList(score, s, t));
        }
        return res;
    }

    /**
     * 部分対応のスコア
     *
     * @param source
     * @param target
     * @return
     */
    private int dp_c_score(List<String> source, List<String> target) {
        // 辞書を引くために、特殊文字に置き換える
        String s = "*";
        if (!source.isEmpty()) {
            s = String.join("", source);
        }
        String t = "*";
        if (!target.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String x : target) {
                if (x.equals(" ")) {
                    sb.append("@");
                } else {
                    sb.append(x);
                }
            }
            t = sb.toString();
        }
        int res = dp_score_table(s, t);
        if (res > 0) {
            return res;
        }
        return dp_c_score_default(source, target);
    }

    /**
     * 対応関係がテーブルに登録されている場合
     *
     * @param s
     * @param t
     * @return
     */
    private int dp_score_table(String s, String t) {
        if (table.containsKey(s + ":" + t)) {
            return table.get(s + ":" + t);
        } else {
            return dp_score_table_downcase(s, t);
        }
    }

    /**
     * ja側を小文字にすればテーブルに登録されている場合
     *
     * @param s
     * @param t
     * @return
     */
    private int dp_score_table_downcase(String s, String t) {
        if (table.containsKey(s.toLowerCase() + ":" + t)) {
            int x = table.get(s.toLowerCase() + ":" + t);

            int cnt = 0;
            for (char ch : new char[]{'A', 'I', 'U', 'E', 'O', 'J', 'W'}) {
                for (String ss : s.split("")) {
                    cnt += (int) ss.chars().filter(c -> c == ch).count();
                }
            }
            return x + cnt * 250;
        }
        return 0;
    }

    /**
     * 対応関係がテーブルに登録されていない場合 => 100点以下
     *
     * @param source
     * @param target
     * @return
     */
    private int dp_c_score_default(List<String> source, List<String> target) {
        String ja = charApi.ja_type_string(source);
        String en = charApi.en_type_string(target);
        if (ja.equals("v")) {    // 子音の後の単独の母音
            if (en.isEmpty()) {  // 母音の湧き出し
                return 60;
            } else if (en.matches("^v+$")) { // 相手側も母音
                return 50;
            } else {
                return 0;
            }
        } else if (ja.matches("^[Vv]l?$")) { // 母音
            if (en.matches("^v+$")) {  // 母音の湧き出し
                return 50;
            } else {
                return 0;
            }
        } else if (ja.matches("^[Cs]+$")) { // 子音, 半母音
            if (en.matches("^[c]+$")) {   // 相手側が子音
                return 50;
            } else {
                return 0;
            }
        } else if (ja.matches("^Cs?Vl?$")) { // カタカナ音
            if (en.matches("^c+v+$")) {
                return 100;
            } else {
                return 0;
            }

        } else if (ja.isEmpty()) {
            if (en.equals("c")) {  // アルファベット側の読まない子音
                return 30;
            } else {
                return 0;
            }
        }
        return 0;
    }
}
