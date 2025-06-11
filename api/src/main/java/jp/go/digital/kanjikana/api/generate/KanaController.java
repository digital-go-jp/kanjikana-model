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

package jp.go.digital.kanjikana.api.generate;

import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.Response;
import jp.go.digital.kanjikana.core.executor.generate.Kanji2Kana;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
/**
 * 漢字からカナを推計するインターフェース
 *
 * <p>入力</p>
 * <pre>{@code
 * /kana?kanji=山田　太郎&max=5
 * kanji: 入力する漢字姓名
 * max: 推計するカナ姓名の最大候補数
 * }
 * </pre>
 * <p>出力</p>
 * <pre>{@code
 *   {
 *     "response": "OK",
 *     "result": {
 *        "probability": [
 *            0.9996072536026324,
 *            9.137487255267813e-11,
 *            4.024390209880103e-12,
 *            1.1804412427592164e-12,
 *            5.451497709046935e-13
 *         ],
 *         "kana": [
 *            "ヤマダタロウ",
 *            "ヤマダダイタロウ",
 *            "ヤマダコウタロウ",
 *            "ヤマダダイロウ",
 *            "ヤマダヒロオ"
 *          ],
 *          "status": "OK"
 *       },
 *       "version": "1.1"
 *   }
 *   }
 *
 *   response: OK or NG
 *   result.probability: 推計したカタカナ名の確率　高いほど良い
 *   result.kana: 推計したカタカナ名
 * </pre>
 */
@RestController
public class KanaController {
    private static final Logger logger = LogManager.getLogger(KanaController.class);

    @RequestMapping(value = "/kana", method = RequestMethod.GET)
    @CrossOrigin
    public Object kanji(@RequestParam(value = "kanji", defaultValue = "") String kanji, @RequestParam(value = "max", defaultValue = "5") String n_best) {
        try {
            logger.debug("KanaController;kanji="+kanji+",max="+n_best);
            Kanji2Kana kk = new Kanji2Kana();
            Output o = kk.exec(kanji, Integer.parseInt(n_best));
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
