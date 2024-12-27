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

package jp.go.digital.kanjikana.core.engine.dict.impl;

import jp.go.digital.kanjikana.core.engine.dict.Dict;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.utils.Moji;
import jp.go.digital.kanjikana.core.Resources;

/**
 * クロールで集めた姓名辞書を保持するシングルトンクラス
 *  小書き文字を大書文字へ変換と全銀協で使用できない文字を変換する　Moji.normalizeで定義
 * 最も信頼度が高い辞書
 */
public class DictCrawlNormalized extends Dict {
    private static final String DefaultFile = Resources.getProperty(Resources.PropKey.DIC_CRAWL);

    private static DictCrawlNormalized dict = null;
    private DictCrawlNormalized() throws Exception {
        super(DefaultFile, true);
    }

    /**
     * クロール辞書を保持するクラスを返す
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictCrawlNormalized();
        }
        return dict;
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(Moji.normalize(key));
    }

    @Override
    public boolean containsValueKey(String key, String valueKey) {
        return super.containsValueKey(key, Moji.normalize(valueKey));
    }
}
