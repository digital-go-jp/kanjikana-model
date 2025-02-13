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

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;
import jp.go.digital.kanjikana.core.engine.WordEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawl;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawlNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSS;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSSNormalized;
import jp.go.digital.kanjikana.core.model.AbstWordModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;

import java.util.Arrays;

/**
 * クロール , OSS辞書を用いて単語単位で突合する，最も信頼度高い
 *
 */
public class CrawlOSSWordModel extends AbstWordModel  {

    public CrawlOSSWordModel() throws Exception {
        super(new WordEngine(Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance(), DictCrawl.newInstance(), DictOSS.newInstance(), DictCrawlNormalized.newInstance(), DictOSSNormalized.newInstance()), false));
    }

    public CrawlOSSWordModel(EngineIF engine) throws Exception{
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
    protected ResultEngineParts run_sub(String kanji_parts, String kana_parts) throws Exception {
        ResultEngineParts topResult = getEngine().check(kanji_parts, kana_parts);
        return topResult;
    }
}