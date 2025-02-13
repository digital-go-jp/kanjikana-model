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

package jp.go.digital.kanjikana.core.engine.foreigner.lib;

import java.util.Arrays;

public final class Char extends CharApi {

    {
        // // 英語表記で使用する文字の定義
        define_char(en_char, Arrays.asList("a", "i", "u", "e", "o"), Type.v);                               // 母音字
        define_char(en_char, Arrays.asList("b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "x", "y", "w", "z"), Type.c);  // 子音字
        define_char(en_char, Arrays.asList(" "), Type.X); // 特殊記号
        define_char(en_char, Arrays.asList("=", "-"), Type.X);   // 特殊記号
        define_char(en_char, Arrays.asList("'"), Type.X);                    // 特殊文字

        //
        // ローマ字表記で使用する文字の定義
        //
        // V, C, Q, Xが、ローマ字表記のカタカナ単位の先頭
        define_char(ja_char, Arrays.asList("A", "I", "U", "E", "O"), Type.V);      // 母音字
        define_char(ja_char, Arrays.asList("a", "i", "u", "e", "o"), Type.v);      // 母音字(子音字に続く or 小書)
        define_char(ja_char, Arrays.asList("j", "w"), Type.s);         // 半母音字(子音字に続く)
        define_char(ja_char, Arrays.asList("K", "G", "S", "1", "Z", "3", "T", "C", "2", "D", "H", "F", "V", "B", "P", "M", "R", "N", "J", "W", "0"), Type.C);  // 子音字
        define_char(ja_char, Arrays.asList("+"), Type.l);    // 長音記号「ー」
        define_char(ja_char, Arrays.asList("!"), Type.Q);      // 促音「ッ」
        define_char(ja_char, Arrays.asList("@", "=", "-"), Type.X);    // 特殊記号

    }
}
