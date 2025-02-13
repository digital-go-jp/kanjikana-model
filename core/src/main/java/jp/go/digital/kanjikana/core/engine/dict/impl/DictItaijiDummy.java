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

import jp.go.digital.kanjikana.core.engine.ResultAttr;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;

import java.util.List;

/**
 * 何もしない辞書クラス。シングルトン。
 * 異体字を使わない場合に，代わりにこれを入れることで，異体字を使わないようにできる
 */
public class DictItaijiDummy implements DictIF {
    private static DictItaijiDummy dict=null;
    private DictItaijiDummy(){}

    /**
     * ダミー辞書クラスを得る
     * @return ダミー辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictItaijiDummy();
        }
        return dict;
    }
    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public boolean containsValueKey(String key, String valueKey) {
        return false;
    }

    @Override
    public List<String> getValue(String key) {
        return null;
    }

    @Override
    public ResultAttr getAttr(String key, String valueKey) {
        return new ResultAttr();
    }

    @Override
    public int getMaxKeyLen() {
        return 0;
    }

    @Override
    public int getMaxValLen() {
        return 0;
    }

    @Override
    public boolean isNormalized() {
        return false;
    }
}
