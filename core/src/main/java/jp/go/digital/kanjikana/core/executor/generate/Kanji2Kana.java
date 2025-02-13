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

package jp.go.digital.kanjikana.core.executor.generate;

import jp.go.digital.kanjikana.core.engine.ai.AiKanjiKanaModels;
import jp.go.digital.kanjikana.core.engine.ai.BeamSearch;
import jp.go.digital.kanjikana.core.engine.ai.SearchResult;
import jp.go.digital.kanjikana.core.utils.Moji;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * AIを用いて漢字からカナの候補を作成するクラス
 */
public class Kanji2Kana {
    private static final Logger logger = LogManager.getLogger(Kanji2Kana.class);

    /**
     * 漢字姓名を与えて，カナ姓名の一覧を得る
     * @param kanji 漢字姓名 「山田」
     * @param n_best  何個，候補を取得するか
     * @return カナ姓名の候補一覧，最大n_best個を返す
     * @throws Exception 一般的なエラー
     */
    public List<SearchResult> run(String kanji, int n_best) throws Exception{
        BeamSearch search = new BeamSearch(AiKanjiKanaModels.newInstance());
        List<SearchResult> res = search.run(n_best, n_best, Moji.normalize_basic(kanji.replace("　","")));
        search.close();
        return res;
    }

    /**
     * 漢字姓名を与えて，カナ姓名の一覧を得る。出力形式をWeb用にした
     * @param kanji 漢字姓名 「山田」
     * @param n_best  何個，候補を取得するか
     * @return カナ姓名の候補一覧，最大n_best個を返す
     * @throws Exception 一般的なエラー
     */
    public Output exec(String kanji, int n_best) throws Exception{
        if(kanji==null || kanji.isEmpty()){
            Output o = new Output();
            o.response = Response.E001;
            return o;
        }

        if(n_best<=0 || n_best>10){
            Output o = new Output();
            o.response = Response.E010;
            return o;
        }
        List<SearchResult> res = run(kanji, n_best);
        return OutputMaker.exec(res, OutputMaker.GenKey.KANA);
    }
}
