/*
 * MIT License
 *
 * Copyright (c) 2025 デジタル庁
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


package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.engine.NandokuEngine;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;
import jp.go.digital.kanjikana.core.model.AbstWordModel;
import jp.go.digital.kanjikana.core.model.ModelData;

import java.util.Arrays;

/**
 * 法務省の難読名許容の指針
 * 置き字、漢字の読みの一部、熟字訓を許容する
 * 単語単位で実施 「渡辺 雲母」「わたなべ きらら」 渡辺ーわたなべ、雲母ーきらら でチェック
 * @version 1.8 DictをNormalizedから通常にした。禁則処理の対応のため
 * @since 1.7
 */
class NandokuWordModel extends AbstWordModel {


    public NandokuWordModel() throws Exception{
        super(new NandokuEngine(Arrays.asList( DictAsIs.newInstance(),  DictTankanji.newInstance()),false));
    }

    public NandokuWordModel(EngineIF engine){
        super(engine);
    }

    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(kanji_item, kana_item, modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData = exec(kanji_item, kana_item, modelData);
        modelData.setModel(this.getClass());
        setStatus(modelData);
        modelData.setModel(this.getClass());
        return modelData;
    }

    @Override
    protected ResultEngineParts run_sub(String kanji_parts, String kana_parts) throws Exception{
        ResultEngineParts topResult = getEngine().check(kanji_parts,kana_parts);
        return topResult;
    }
}
