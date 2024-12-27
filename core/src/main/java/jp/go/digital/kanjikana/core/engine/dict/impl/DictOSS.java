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

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.dict.Dict;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;

/**
 * オープンソース辞書を保持するシングルトンクラス
 * ipadic, mozc, skkの人名辞書から作成している，信頼度高い
 */
public class DictOSS extends Dict {
    private static final String DefaultFile = Resources.getProperty(Resources.PropKey.DIC_OSS);

    private static DictIF dict = null;

    private DictOSS() throws Exception {
        super(DefaultFile, false);
    }

    /**
     * 辞書を得る
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictOSS();
        }
        return dict;
    }
}

