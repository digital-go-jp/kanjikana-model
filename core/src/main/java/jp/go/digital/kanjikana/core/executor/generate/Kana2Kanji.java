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

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.ai.generate.AiKanaKanjiEngine;
import jp.go.digital.kanjikana.core.engine.ai.generate.BeamSearch;
import jp.go.digital.kanjikana.core.engine.ai.generate.SearchResult;
import jp.go.digital.kanjikana.core.utils.Moji;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AIを用いて，カナから漢字の候補を作成するクラス
 */
public class Kana2Kanji {
    private static final Logger logger = LogManager.getLogger(Kana2Kanji.class);
    private final static int TIMEOUT_SECOND= Integer.parseInt(Resources.getProperty(Resources.PropKey.TIMEOUT_SECOND));
    /**
     * カナ姓名を与えて，漢字姓名の一覧を得る, watchdog追加 ver2.0
     * @param kana カナ姓名　「ヤマダ」
     * @param n_best 何個，候補を取得するか
     * @return 漢字姓名の候補一覧，最大n_best個を返す
     * @throws Exception 一般的なエラー
     *
     */
    List<SearchResult> run(String kana, int n_best) throws Exception {
        ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            TimeLimiter limiter = SimpleTimeLimiter.create(es);
            return limiter.callWithTimeout(() -> run_sub(kana, n_best), TIMEOUT_SECOND, TimeUnit.SECONDS);
        } finally {
            es.shutdownNow();
        }
    }

    /**
     * カナ姓名を与えて，漢字姓名の一覧を得る
     * @param kana カナ姓名　「ヤマダ」
     * @param n_best 何個，候補を取得するか
     * @return 漢字姓名の候補一覧，最大n_best個を返す
     * @throws Exception 一般的なエラー
     */
    private List<SearchResult> run_sub(String kana, int n_best) throws Exception{
        BeamSearch search = new BeamSearch(AiKanaKanjiEngine.newInstance());
        try {
            return search.run(n_best, n_best, Moji.normalize_basic(kana.replace("　","")));
        } finally {
            search.close();
        }
    }

    /**
     * カナ姓名を与えて，漢字姓名の一覧を得る。出力形式をWeb用にした
     * @param kana カナ姓名　「ヤマダ」
     * @param n_best 何個，候補を取得するか
     * @return 漢字姓名の候補一覧，最大n_best個を返す
     * @throws Exception 一般的なエラー
     */
    public Output exec(String kana, int n_best) throws Exception{
        if(kana==null || kana.isEmpty()){
            Output o = new Output();
            o.response = Response.E002;
            return o;
        }
        if(n_best<=0 || n_best>10){
            Output o = new Output();
            o.response = Response.E010;
            return o;
        }
        try {
            List<SearchResult> res = run(kana, n_best);
            return OutputMaker.exec(res, OutputMaker.GenKey.KANJI);
        }catch(TimeoutException e){
            Output o = new Output();
            o.response = Response.E101;
            return o;
        }
    }
}
