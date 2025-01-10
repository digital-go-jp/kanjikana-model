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

import jp.go.digital.kanjikana.core.engine.WordEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawl;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawlNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSS;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSSNormalized;
import jp.go.digital.kanjikana.core.model.ModelData;

import java.util.Arrays;

/**
 * 単語単位でOSS辞書辞書モデルを用いる（信頼度高い），その時に，異体字辞書も同時に適用し，辞書に異体字がない場合にでもマッチできるようにしている
 * 単語単位で辞書と比較する
 * 漢字姓名「渡　邉一郎」
 * カナ姓名「ワタナベ　イチロウ」
 *
 * 異体字辞書には　「辺」と「邉」の変換が定義されているため，辞書には「渡辺」と「ワタナベ」のペアしかなくても
 * 異体字辞書で順次「邉」を「辺」に置き換えていくことで辞書とマッチできる
 *
 */
public final class ICrawlOSSWordModel extends CrawlOSSWordModel {

    public ICrawlOSSWordModel() throws Exception{
        super(new WordEngine(Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance(), DictCrawl.newInstance(), DictOSS.newInstance(), DictCrawlNormalized.newInstance(), DictOSSNormalized.newInstance()), true));
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        ModelData md =  super.run(kanji_item,kana_item,modelData);
        md.setModel(this.getClass());
        return md;
    }
}