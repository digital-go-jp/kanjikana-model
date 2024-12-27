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

package jp.go.digital.kanjikana.core.executor.match;

import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyExecutor;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.executor.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * WebAppから呼び出し用のクラス
 */
public class KanjiKanaMatch {
    private static final Logger logger = LogManager.getLogger(KanjiKanaMatch.class);


    private final StrategyExecutor executor;

    public KanjiKanaMatch(StrategyIF strategy){
        executor = new StrategyExecutor(strategy);
    }

    /**
     * 漢字姓名とカナ姓名を与えて，一致しているかどうかを判定する。Web用の出力
     * @param kanji 漢字姓名
     * @param kana カナ姓名
     * @return 判定結果
     * @throws Exception 一般的なエラー
     */
    public Output exec(String kanji, String kana) throws Exception{

        if(kanji==null || kanji.isEmpty()){
            Output o = new Output();
            o.response = Response.E001;
            return o;
        }
        if(kana==null || kana.isEmpty()){
            Output o = new Output();
            o.response = Response.E002;
            return o;
        }
        return  executor.exec(kanji, kana);
    }
}
