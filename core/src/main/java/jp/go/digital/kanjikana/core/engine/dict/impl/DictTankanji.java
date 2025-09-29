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

import java.util.Arrays;
import java.util.List;

/**
 * 単漢字辞書を保持するシングルトンクラス
 * mjから作成
 * 漢字とカナの文字列から，徐々に切り出しつつ，通常辞書と単漢字辞書などを組み合わせながら読み仮名があっているか推測していくため
 */
public class DictTankanji extends Dict {
    protected static final String DefaultFile = Resources.getProperty(Resources.PropKey.DIC_TANKANJI);
    protected static DictIF dict=null;

    protected DictTankanji(boolean normalized) throws Exception {
        super(DefaultFile, normalized);
    }

    /**
     * 辞書を得る
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictTankanji(false);
        }
        return dict;
    }
}
