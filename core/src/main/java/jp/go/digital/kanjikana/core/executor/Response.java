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

package jp.go.digital.kanjikana.core.executor;


/**
 * プログラム実行時レスポンスを保持する
 * プログラム実行時にエラーが発生した場合にはEXXXを返す。
 */
public enum  Response {
    OK("OK"),
    E001("kanji not set"),// kanjiが未設定
    E002("kana not set"),// kanaが未設定
    E010("max is not between 0 to 10"), //
    E100("internal error"),// 内部エラー
    E404("not found"), // 404 error
    E500("application error"), // 500 error
    ;

    private final String val;

    Response(String val) {
        this.val = val;
    }

    /**
     * Enumの文字列表現を返す
     * @return 文字列表現
     */
    public String getVal() {
        return this.val;
    }

    /**
     * 値に合致する enum 定数を返す。
     * @param s Enumの文字列表現
     */
    public static Response getType(String s) {
        // 値から enum 定数を特定して返す処理
        for (Response value : Response.values()) {
            if (value.getVal().equals(s)) {
                return value;
            }
        }
        return null; // 特定できない場合
    }
}
