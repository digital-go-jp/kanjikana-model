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

package jp.go.digital.kanjikana.api.match;

import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.Response;
import jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatch;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyBasic;
import jp.go.digital.kanjikana.core.executor.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 漢字姓名とカナ姓名がマッチしているかどうかを判定するインターフェース
 * 簡易モデル
 *
 * 信頼度の高い漢字カナの辞書を用いてマッチング判定
 *
 * <p>入力</p>
 * <pre>{@code
 * /detail?kana=ヤマダ　タロウ&kanji=山田　太郎
 * kana: 入力するカナ姓名
 * kanji: 入力する漢字姓名
 * }
 * </pre>
 * <p>出力</p>
 * <pre>{@code
 * {
 *     "response": "OK",
 *     "result": {
 *         "status": 90
 *     },
 *     "version": "1.1"
 *   }
 *
 *   response: OK or EXXX
 *   result.status: 漢字姓名とカナ姓名の一致度合い　0-99の値を取る。50未満が不一致と判定
 *   }
 * </pre>
 */
@RestController
public class SimpleController {
    private static final Logger logger = LogManager.getLogger(SimpleController.class);

    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    @CrossOrigin
    public Object simple(@RequestParam(value = "kanji", defaultValue = "") String kanji, @RequestParam(value = "kana", defaultValue = "") String kana, @RequestParam(value = "debug", defaultValue = "") String debug) {
        logger.debug("SimpleController;kanji="+kanji+",kana="+kana);

        try {
            KanjiKanaMatch match = new KanjiKanaMatch(StrategyBasic.newInstance());
            Output o =  match.exec(kanji, kana);
            if(debug.isEmpty()){
                o.result.getAdditionalProperties().remove(OutputMaker.ADDITIONAL_KEY_NOTES);
            }
            return o;
        } catch (Exception e) {
            e.fillInStackTrace();
            logger.fatal(e);
            Output o = new Output();
            o.response = Response.E100;
            return o;
        }
    }
}
