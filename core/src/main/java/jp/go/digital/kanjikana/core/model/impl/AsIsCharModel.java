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

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.engine.CharEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;

import java.util.Arrays;

/**
 * 漢字とカナが同じものを一致として返すモデル
 * 文字単位での検査。
 * 漢字姓名「山田　めぐみ」
 * カナ姓名「ヤマ　ダメグミ」
 *
 * 漢字「めぐみ」
 * カナ「メグミ」
 * これらを拾うモデル
 */
public final class AsIsCharModel  extends DictCharModel {

    public AsIsCharModel() throws Exception {
        super(new CharEngine(Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance()),false));
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        ModelData md = super.run(kanji_item, kana_item, modelData);
        md.setModel(this.getClass());
        return md;
    }
}
