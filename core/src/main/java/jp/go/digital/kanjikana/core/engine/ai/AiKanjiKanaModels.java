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

package jp.go.digital.kanjikana.core.engine.ai;

import jp.go.digital.kanjikana.core.Resources;

/**
 * Pythonで作成した漢字からカナへ変換するAIモデルの，モデルデータを保持するクラス，シングルトン
 */
public final class AiKanjiKanaModels extends AiModels{
    private static AiKanjiKanaModels models=null;
    private static final String VocabSrc = Resources.getProperty(Resources.PropKey.AI_SRCVOCAB);
    private static final String VocabTgt = Resources.getProperty(Resources.PropKey.AI_TGTVOCAB);
    private static final String Decoder = Resources.getProperty(Resources.PropKey.AI_DECODER);
    private static final String Encoder = Resources.getProperty(Resources.PropKey.AI_ENCODER);
    private static final String Params = Resources.getProperty(Resources.PropKey.AI_PARAMS);
    private static final String PositionEnc = Resources.getProperty(Resources.PropKey.AI_POSENC);
    private static final String Generator = Resources.getProperty(Resources.PropKey.AI_GENERATOR);
    private static final String SrcTokEmb = Resources.getProperty(Resources.PropKey.AI_SRCEMB);
    private static final String TgtTokEmb = Resources.getProperty(Resources.PropKey.AI_TGTEMB);

    private AiKanjiKanaModels(){
        super(VocabSrc, VocabTgt, PositionEnc, Params, Encoder, Decoder, Generator, SrcTokEmb, TgtTokEmb);
    }

    /**
     * シングルトンのインスタンスを取得する
     * @return モデルパラメタ
     */
    public synchronized static AiKanjiKanaModels newInstance(){
        if(models==null){
            models = new AiKanjiKanaModels();
        }
        return models;
    }
}
