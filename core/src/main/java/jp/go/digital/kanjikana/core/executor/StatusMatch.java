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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * jp.go.digital.kanjikana.core.executor.match　以下を用いて作成した結果を表示する
 */
public enum StatusMatch {
    OK(90),
    ENSEMBLE3(80),
    ENSEMBLE2(70),
    ENSEMBLE1(30),
    NG(0);

    private final int val;

    StatusMatch(int val) {
        this.val = val;
    }

    public static final int THRESHOLD=50; // 50未満をNGとする

    /**
     * 結果がOKかどうか
     * @param statusMatch　結果
     * @return　OKならばTrue
     */
    public static boolean isOk(StatusMatch statusMatch){
        if(statusMatch.toValue()>=THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    @JsonValue
    public int toValue(){ // JSON出力の際に，数字で出すため
        return val;
    }
}
