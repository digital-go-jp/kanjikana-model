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

import java.util.Arrays;
import java.util.List;

/**
 * Dictではなく，漢字部分にカタカナやひらがなが入ってきた時にマッチさせるクラス。シングルトン
 * 漢字姓名「あゆみ」
 * カナ姓名「アユミ」
 * をマッチさせる
 */
public class DictAsIs implements DictIF {
    private static DictAsIs dict=null;
    private DictAsIs(){}

    /**
     * 漢字姓名に含まれるひらがなカタカナを，カタカナ姓名とマッチさせるクラスを取得する
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictAsIs();
        }
        return dict;
    }

    @Override
    public boolean containsKey(String key) {
        return true; // 常に検査するので
    }

    @Override
    public boolean containsValueKey(String key, String valueKey) {
        return key.equals(valueKey);
    }

    @Override
    public List<String> getValue(String key) {
        return Arrays.asList(key); // そのまま返す
    }

    @Override
    public ResultAttr getAttr(String key, String valueKey) {
        return new ResultAttr();
    }

    @Override
    public int getMaxKeyLen() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxValLen() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isNormalized() {
        return false;
    }
}
