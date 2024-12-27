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

package jp.go.digital.kanjikana.core.model;


/**
 * モデルのIF、漢字姓名とかな姓名を入力し、一致しているかどうか判定
 * モデルによっては、スペースを削除したりする事も行う
 */
public interface ModelIF {

    /**
     * モデルを実行する
     * モデル実行する漢字とカナはmodelDataのgetKanjiとgetKanaで取得したものをもとに行う
     * @param modelData 前回のモデルでの結果，この結果をもとに，マッチしていない部分についてこのモデルでチェックする
     * @return Result 突合結果
     * @throws Exception general exceptions
     */
    ModelData run(ModelData modelData)throws Exception;

    /**
     * モデルを実行する
     * モデル実行する漢字とカナはkanjiとkanaをもとに行う
     * @param kanji 漢字姓名　「山田　太郎」
     * @param kana カナ姓名　「ヤマダ　タロウ」
     * @param modelData 前回のモデルでの結果，この結果をもとに，マッチしていない部分についてこのモデルでチェックする
     * @return Result 突合結果
     * @throws Exception general exceptions
     */
    ModelData run(String kanji, String kana, ModelData modelData)throws Exception;
}
