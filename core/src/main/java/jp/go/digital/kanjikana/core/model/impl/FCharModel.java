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
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.engine.FCharEngine;
import jp.go.digital.kanjikana.core.engine.foreigner.Foreigner;

/**
 * 外国人モデルを用いて，文字単位で検査する
 * 漢字姓名「ＢＩＬＬ　ＹＪＯＥＬ」
 * カナ姓名「ビリー　ジョエル」
 * これはマッチする
 *
 *
 */
public final class FCharModel extends AbstCharModel {

    public FCharModel() throws Exception{
        super(new FCharEngine());
    }

    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(kanji_item, kana_item, modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData.setModel(this.getClass());
        if (!Foreigner.en_string_check(kanji_item)) {
            return modelData; // 外人でないのでそのまま返す
        }
        // 全部やり直す
        //////modelData.setTopResult(new ResultEngineParts(kanji_item,kana_item));

        ModelData md = exec(kanji_item, kana_item, modelData);
        md.setModel(this.getClass());
        return md;
    }
    @Override
    protected ResultEngineParts run_sub(String kanji_str, String kana_str) throws Exception{

        // 外国人モデルはスペースなしで検査
        return super.run_sub(kanji_str.replace("　",""),kana_str.replace("　",""))
        ;
    }
}
