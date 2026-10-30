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

/**
 * AIを用いて文字単位で検査する
 * AiClassifyCharEngine, AiGenerateCharEngineを外国人、日本人で使い分ける
 */
public final class AiCharEngine extends AbstEngine {
    private final AiGenerateCharEngine generate_engine;
    private final AiClassifyCharEngine classify_engine;

    public AiCharEngine(boolean hasItaiji) throws Exception{
        generate_engine = new AiGenerateCharEngine(hasItaiji);
        classify_engine = new AiClassifyCharEngine(hasItaiji);
    }

    @Override
    public boolean isValidEngine(){
        return true;
    }
    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part 田中　太郎
     * @param kana_part タナ　カタロウ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part, String kana_part){
        if (FJClassify.isForeigner(kanji_part)) {
            return classify_engine.check(kanji_part,kana_part);
        }else{
            return generate_engine.check(kanji_part,kana_part);
        }
    }
}
