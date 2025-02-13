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
 * Pythonで作成したカナから漢字へ変換するAIモデルの，モデルデータを保持するクラス，シングルトン
 */
public final class AiKanaKanjiModels extends AiModels{
    private static AiKanaKanjiModels models=null;
    private static final String VocabSrc_r = Resources.getProperty(Resources.PropKey.AI_R_SRCVOCAB);
    private static final String VocabTgt_r = Resources.getProperty(Resources.PropKey.AI_R_TGTVOCAB);
    private static final String Decoder_r = Resources.getProperty(Resources.PropKey.AI_R_DECODER);
    private static final String Encoder_r = Resources.getProperty(Resources.PropKey.AI_R_ENCODER);
    private static final String Params_r = Resources.getProperty(Resources.PropKey.AI_R_PARAMS);
    private static final String PositionEnc_r = Resources.getProperty(Resources.PropKey.AI_R_POSENC);
    private static final String Generator_r = Resources.getProperty(Resources.PropKey.AI_R_GENERATOR);
    private static final String SrcTokEmb_r = Resources.getProperty(Resources.PropKey.AI_R_SRCEMB);
    private static final String TgtTokEmb_r = Resources.getProperty(Resources.PropKey.AI_R_TGTEMB);

    private AiKanaKanjiModels(){
        super(VocabSrc_r, VocabTgt_r, PositionEnc_r, Params_r, Encoder_r, Decoder_r, Generator_r, SrcTokEmb_r, TgtTokEmb_r);
    }

    /**
     * シングルトンのインスタンスを取得する
     * @return モデルパラメタ
     */
    public static synchronized AiKanaKanjiModels newInstance(){
        if(models==null){
            models = new AiKanaKanjiModels();
        }
        return models;
    }
}
