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

import jp.go.digital.kanjikana.core.engine.foreigner.Aligner;
import jp.go.digital.kanjikana.core.engine.foreigner.ParamsForeigner;
import jp.go.digital.kanjikana.core.engine.foreigner.ResultAwase;

import java.util.List;
import java.util.stream.Collectors;

public class Api {
    private final Romaji romaji = new Romaji();
    private final Char chars = new Char();

    private final Aligner aligner;

    public Api() throws Exception {
        aligner = new Aligner();
    }

    /**
     * カタカナ-ローマ字変換
     *
     * @param ja
     * @param aligned
     * @return
     * @throws Exception
     */
    private List<List<String>> katakana2romaji(String ja, boolean aligned) throws Exception {
        if (!aligned) {
            throw new Exception("invalid arguments");
        }
        return romaji.k2r(ja, true);
    }

    private List<List<String>> k2r(String ja, boolean aligned) throws Exception {
        return katakana2romaji(ja, true);
    }

    private String katakana2romaji(String ja) throws Exception {
        return romaji.k2r(ja);
    }

    private String k2r(String ja) throws Exception {
        return katakana2romaji(ja);
    }


    /**
     * ローマ字-カタカナ変換
     *
     * @param ja
     * @param align
     * @return
     * @throws Exception
     */
    private List<List<String>> romaji2katakana(String ja, boolean align) throws Exception {
        return romaji.r2k(ja, true);
    }

    private List<List<String>> r2k(String ja, boolean align) throws Exception {
        return romaji2katakana(ja, align);
    }

    private String romaji2katakana(String ja) throws Exception {
        return romaji.r2k(ja);
    }

    private String r2k(String ja) throws Exception {
        return romaji2katakana(ja);
    }

    /**
     * カタカナ正規化
     */
    private String normalize_katakana(String ja) throws Exception {
        return romaji.k2k(ja);
    }

    private String k2k(String ja) throws Exception {
        return normalize_katakana(ja);
    }

    private List<List<String>> normalize_katakana(String ja, boolean align) throws Exception {
        return romaji.k2k(ja, align);
    }

    private List<List<String>> k2k(String ja, boolean align) throws Exception {
        return normalize_katakana(ja, align);
    }

    /**
     * データチェック
     *
     * @param ja
     * @param en
     * @param params
     * @return
     */

    private boolean doubtful(String ja, String en, ParamsForeigner params) throws Exception {
        return not_norm_string(ja, en);
    }

    private boolean check(String ja, String en, ParamsForeigner params) throws Exception {
        return doubtful(ja, en, params);
    }

    private boolean cleaning(String ja, String en, ParamsForeigner params) throws Exception {
        return doubtful(ja, en, params);
    }

    /**
     * 文字列レベルの非規範性チェック
     *
     * @param ja
     * @param en
     * @return
     */
    private boolean not_norm_string(String ja, String en) throws Exception {
        if (!chars.valid_en_string(en)) {
            return false;
        } else if (!chars.valid_ja_string(ja)) {
            return false;
        } else if (!k2k(ja).equals(ja)) {
            return false;
        }
        return true;
    }

    public ResultAwase awase(String ja, String en, ParamsForeigner params) throws Exception {
        return awase_sub(ja, en, params);
    }


    private ResultAwase awase_sub(String ja, String en, ParamsForeigner params) throws Exception {
        boolean error = not_norm_string(ja, en);    // 規範チェック
        if (!error) {
            return new ResultAwase();
        }
        ResultAwase res = make_alignment(ja, en, params);// アライメントの実行
        return res;
    }

    private ResultAwase make_alignment(String ja, String en, ParamsForeigner params) throws Exception {
        en = en.toLowerCase().replace(" ", "@");   // 英語綴
        List<List<String>> ka = k2r(ja, true); // [[カタカナ, ローマ字], ...]
        ;
        return aligner.awase(ka, String.join("", ka.stream().map(x -> x.get(1)).collect(Collectors.toList())), en, params);

    }

}
