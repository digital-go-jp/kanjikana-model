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
package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.utils.FJClassify;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AIを用いて単語単位で突合するエンジン
 */
public class AiWordEngine extends  AbstEngine{
    private static final Logger logger = LogManager.getLogger(AiWordEngine.class);

    private final AiGenerateWordEngine generate_engine;
    private final AiClassifyWordEngine classify_engine;
    public AiWordEngine(boolean hasItaiji) throws Exception{
        generate_engine = new AiGenerateWordEngine(hasItaiji);
        classify_engine = new AiClassifyWordEngine(hasItaiji);
    }

    @Override
    public boolean isValidEngine(){
        return true;
    }


    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part_orig 田中　太郎
     * @param kana_part タナカ　タロウ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part_orig, String kana_part){
        if (FJClassify.isForeigner(kanji_part_orig)) {
            return classify_engine.check(kanji_part_orig,kana_part);
        }else{
            return generate_engine.check(kanji_part_orig,kana_part);
        }
    }
}
