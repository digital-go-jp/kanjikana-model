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
import jp.go.digital.kanjikana.core.executor.generate.Kana2Kanji;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * カナから漢字を推計するインターフェース
 *
 * <p>入力</p>
 * <pre>{@code
 * /kanji?kana=ヤマダ　タロウ&max=5
 * kana: 入力するカナ姓名
 * max: 推計する漢字姓名の最大候補数
 * }
 * </pre>
 * <p>出力</p>
 * <pre>{@code
 *   {
 *     "response": "OK",
 *         "result": {
 *             "kanji": [
 *                 "山田太郎",
 *                 "山田太朗",
 *                 "山田多郎",
 *                 "山田大郎",
 *                 "山田多朗"
 *              ],
 *              "probability": [
 *                  0.7653920292536642,
 *                  0.0031187462187909293,
 *                  0.00013534597288438163,
 *                  0.000001452063890384118,
 *                  7.120255678830607e-7
 *               ],
 *               "status": "OK"
 *           },
 *       "version": "1.1"
 *   }
 *   }
 *
 *   response: OK or NG
 *   result.probability: 推計した漢字名の確率　高いほど良い
 *   result.kanji: 推計した漢字名
 * </pre>
 */
@RestController
public class KanjiController {
    private static final Logger logger = LogManager.getLogger(KanjiController.class);

    @RequestMapping(value = "/kanji", method = RequestMethod.GET)
    @CrossOrigin
    public Object kanji(@RequestParam(value = "kana", defaultValue = "") String kana, @RequestParam(value = "max", defaultValue = "5") String n_best) {
        try {
            logger.debug("KanjiController;kanji="+kana+",max="+n_best);
            Kana2Kanji kk = new Kana2Kanji();
            Output o = kk.exec(kana, Integer.parseInt(n_best));
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
