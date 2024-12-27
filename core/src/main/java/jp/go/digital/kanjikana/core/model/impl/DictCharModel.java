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

package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.model.AbstCharModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.engine.CharEngine;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimei;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimeiNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;

import java.util.Arrays;

/**
 *　辞書を用いて，文字単位で分割して検査するモデル
 * 単漢字辞書や調達で作成した辞書も入るので信頼度が落ちる
 * 文字単位なので単漢字辞書なども用いるために，信頼度は低くなる
 */
public class DictCharModel extends AbstCharModel implements ModelIF {

    public DictCharModel() throws Exception{
        this.engine = new CharEngine(Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance(), DictSeimei.newInstance(), DictSeimeiNormalized.newInstance(), DictTankanji.newInstance(), DictTankanjiNormalized.newInstance()),false);
    }

    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(kanji_item, kana_item ,modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData.setModel(this.getClass());
        if(modelData.getTopResult().getKanji().length()==0){
            modelData.setTopResult(new ResultEngineParts(modelData.getKanji(),modelData.getKana()));
        }
        ModelData md = exec(kanji_item, kana_item, modelData);
        md.setModel(this.getClass());
        return md;
    }
}
