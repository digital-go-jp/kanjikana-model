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

import jp.go.digital.kanjikana.core.engine.foreigner.lib.Api;
import jp.go.digital.kanjikana.core.engine.foreigner.lib.Romaji;
import jp.go.digital.kanjikana.core.utils.KanjiKanaUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.terasoluna.gfw.common.fullhalf.DefaultFullHalf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  1. データのチェックに特化する - 4bitの文字列で結果を出力(0: OK, 1: NG) <br/>
 *     1.1 アルファベット表記チェック - 認められた文字のみで構成されているか？ <br/>
 *     1.2 カタカナ表記チェック       - 認められた文字のみで構成されているか？ <br/>
 *     (-> 以降のチェックはせず、すべてNG) <br/>
 *     1.3 イニシャルチェック     - アルファベット側にイニシャルが含まれていないか？ (-> イニシャルは削除) <br/>
 *     1.4 カタカナ標準化チェック - 絣式カタカナ文字列か (-> 絣式カタカナ列に変換する) <br/>
 *     1.5 構成要素数チェック     - 構成要素の数は、両側で等しいか？ (-> アライメントは取ってみる) <br/>
 *     1.6 対応チェック           - 文字列間のアライメントは取れるか <br/>
 *
 *  2. 表記の仕様の違いに対応(認める文字) <br/>
 *     アルファベット表記： 全角Ａ-Ｚ, 全角空白 <br/>
 *     カタカナ表記：       全角ァ-ヴ, 長音記号, 全角空白 <br/>
 *
 *  3. 区切り記号問題 <br/>
 *     アルファベット側の区切り記号の消失を認める (standardの場合) <br/>
 */
public final class Foreigner {
    Logger logger = LogManager.getLogger(Foreigner.class);
    private final Romaji romaji = new Romaji();
    private ParamsForeigner params = null;

    // アルファベット正規表現でチェック
    private static final Pattern ALPHA = Pattern.compile("^[ -A-Z_a-z‐―　ー－．０-９Ａ-Ｚ]+$");

    private final Api api;

    private static final Pattern KATA = Pattern.compile("^[ァ-ヶ　ー・]+$");

    public Foreigner() throws Exception{
        String check = ParamsForeigner.Check.strict;
        boolean raw = true;

        this.params = new ParamsForeigner( 0,  1,  check,  raw,  ",", null);
        this.api = new Api();
    }

    /**
     * カタカナ側のチェック: 全角ァ-ヴ, 長音記号, 全角スペース
     *
     * @param str
     * @return
     */
    public static boolean ja_string_check(String str) {
        Matcher kata = KATA.matcher(str);
        return kata.matches();
    }

    /**
     * アルファベット側のチェック: 全角Ａ-Ｚ, 全角スペース
     *
     * @param str
     * @return
     */
    public static boolean en_string_check(String str) {
        Matcher alpha = ALPHA.matcher(str);
        return alpha.matches();
    }

    /**
     * アルファベット文字列の正規化
     *
     * @param str
     * @return
     */
    private NormStr standardize_en_string(String str) {
        String[] list1 = str.split("　");  // 区切り記号で分割
        List<String> list2 = new ArrayList<>();
        for (String s : list1) { // イニシャルを削除  // TODO
            if (s.length() != 1) {
                list2.add(s);
            }
        }

        StringJoiner sj = new StringJoiner(" ");
        // 半角小文字に変換
        for (String s : list2) {
            sj.add(DefaultFullHalf.INSTANCE.toHalfwidth(s).toLowerCase()); // 半角文字を全角文字へ変換
        }
        String out = sj.toString();

        // [ 正規化した文字列, 構成要素数, イニシャルが含まれていなかったか ]
        return new NormStr(out, list2.size(), list1.length == list2.size());
    }

    /**
     * カタカナ文字列の正規化
     *
     * @param str
     * @return
     */
    private NormStr standardize_ja_string(String str) {
        String str2 = str.replaceAll("　", "・"); // 区切り記号を置換(全角空白 -> 中黒「・」)
        String out = romaji.k2k(str2); //  カタカナ正規化

        // [ 正規化した文字列, 構成要素数, 正規化されたなかったか ]
        return new NormStr(out, str2.split("・").length, str2.equals(out));
    }

    /**
     * コード作成
     *
     * @param list
     */
    private String make_code(boolean... list) {
        StringJoiner sj = new StringJoiner("");
        for (Boolean s : list) {
            if (s) {
                sj.add("0");
            } else {
                sj.add("1");
            }
        }
        return sj.toString();
    }




    public ForeignerOutput run(String kanji_item, String kana_item) throws Exception{

        String line = kanji_item+","+kana_item;
        return run_line(line);
    }

    /**
     *
     * @param line ＢＩＬＬＹ　ＪＯＥＬ＿東京　太郎,ビリー　ジョエル
     * @return
     * @throws Exception
     */
    ForeignerOutput run_line(String line) throws Exception {
        String code = make_code(false, false, false, false, false, false, false);
        ForeignerOutput o = new ForeignerOutput(new ArrayList<>(), code);
        line = line.replace(" ", "　");

        if (line.matches("^\\#.*$") || line.matches("\\s*$")) {
            return o;
        }
        List<String> rec = Arrays.asList(line.split(params.getSep()));
        if(rec.size()<2){
            return o;
        }
        String en_string = rec.get(params.getSource_idx());
        String ja_string = rec.get(params.getTarget_idx());


        // cleaning
        List<String> items = KanjiKanaUtil.kanji_split(en_string);
        for(String kanji:items){
            if(en_string_check(kanji)){

                o = run_sub(kanji, ja_string, rec, true);
                if(!o.getR().doubtful){
                    return o;
                }
                String[] kitems = kanji.split("　");
                // TODO
                if(kitems.length>=4){ // 姓，名，ミドルより多いものは組み合わせで入れ替えを実施しない。組合せ爆発のため
                    continue ;
                }
                ListPairs lp = new ListPairs();
                lp.set(kitems);
                List<List<String>> allList=lp.getAllList();
                for(List<String> nlines: allList){
                    String nline = String.join("　", nlines)+","+ja_string;
                    if (nline.equals(line)){
                        continue;
                    }
                    String newkanji=String.join("　", nlines);
                    o = run_sub(newkanji, ja_string, rec, false);
                    if(!o.getR().doubtful){
                        return o;
                    }
                }
            }
        }

        return o;
    }

    /**
     *
     * @param en_string
     * @param ja_string
     * @param rec
     * @param ordered en_stringが元の順番か，スペースで区切って組み替えていない
     * @return
     * @throws Exception
     */
    private ForeignerOutput run_sub(String en_string, String ja_string, List<String> rec, boolean ordered) throws Exception{
        try {
            /*
            if (line.matches("^\\#.*$") || line.matches("\\s*$")) {
                return null;
            }
            List<String> rec = Arrays.asList(line.split(params.getSeparator()));
            String en_string = rec.get(params.getEnf());
            String ja_string = rec.get(params.getJaf());
            */
            // 文字列チェック
            boolean ens_ok = en_string_check(en_string);
            boolean jas_ok = ja_string_check(ja_string);
            if (!ens_ok || !jas_ok) {
                // 文字列チェックでNG
                String code = make_code(ens_ok, jas_ok, false, false, false, false, false);
                return new ForeignerOutput(rec, code);
            } else {
                NormStr s = standardize_en_string(en_string);
                en_string = s.getNorm_str();
                int en_len = s.getNum_elements();
                boolean en_flag = s.isIs_normalized();

                s = standardize_ja_string(ja_string);
                ja_string = s.getNorm_str();
                int ja_len = s.getNum_elements();
                boolean ja_flag = s.isIs_normalized();
                ResultAwase r = this.api.awase(ja_string, en_string, params);
                String code = make_code(ens_ok, jas_ok, en_flag, ja_flag, en_len == ja_len, ordered, !r.doubtful);
                return new ForeignerOutput(rec, code, params, en_string, ja_string, r);
            }
        }catch(Exception e){
            logger.error(e);
            logger.error(en_string+","+ja_string);
            throw e;
        }
    }

}
