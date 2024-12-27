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

package jp.go.digital.kanjikana.core.engine.dict;

import jp.go.digital.kanjikana.core.engine.ResultAttr;

import java.util.List;

/**
 * 辞書のインターフェース
 */
public interface DictIF {
    /**
     * 辞書にキー（漢字，アルファベット）が存在するかどうか
     * @param key　漢字orアルファベットなど
     * @return 判定結果
     */
    boolean containsKey(String key);

    /**
     * 辞書のキー（漢字，アルファベット）と読み（カタカナ）を与えて，そのペアが存在するかどうか
     * @param key 漢字orアルファベット
     * @param valueKey カタカナ読み
     * @return 判定結果
     */
    boolean containsValueKey(String key, String valueKey);

    /**
     * 辞書のキー（漢字，アルファベット）を与えて，その読みの一覧を返す
     * @param key 漢字orアルファベット
     * @return 読みの一覧
     */
    List<String> getValue(String key);

    /**
     * 辞書のキー（漢字，アルファベット）と読み（カタカナ）を与えて，その属性情報を得る
     * @param key 漢字orアルファベット
     * @param valueKey カタカナ読み
     * @return 属性情報
     */
    ResultAttr getAttr(String key, String valueKey);

    /**
     * 辞書に含まれるキー（漢字，アルファベット）の最大文字数を返す
     * @return 文字数
     */
    int getMaxKeyLen();

    /**
     * 辞書に含まれる読み（カタカナ）の最大文字数を返す
     * @return 文字数
     */
    int getMaxValLen();

    /**
     * このクラスで保持する辞書は正規化されているかどうか
     * @return Trueならば正規化されている，Falseならば辞書JSONそのまま
     */
    boolean isNormalized();

}
