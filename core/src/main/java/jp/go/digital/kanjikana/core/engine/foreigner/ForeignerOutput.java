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

package jp.go.digital.kanjikana.core.engine.foreigner;

import java.util.List;

public final class ForeignerOutput {
    private final List<String> rec ;
    private final String en_string;
    private final String ja_string;
    private final ParamsForeigner params;
    private final String code;
    private final ResultAwase r;

    public ForeignerOutput(List<String> rec, String code, ParamsForeigner params, String en_string, String ja_string, ResultAwase r){
        this.rec = rec;
        this.en_string = en_string;
        this.ja_string = ja_string;
        this.params = params;
        this.code = code;
        this.r = r;
    }
    public ForeignerOutput(List<String> rec, String code){
        this.rec = rec;
        this.en_string = null;
        this.ja_string = null;
        this.params = null;
        this.code = code;
        this.r = new ResultAwase();
    }

    public List<String> getRec() {
        return rec;
    }

    public String getEn_string() {
        return en_string;
    }

    public String getJa_string() {
        return ja_string;
    }

    public ParamsForeigner getParams() {
        return params;
    }

    public String getCode() {
        return code;
    }

    public ResultAwase getR() {
        return r;
    }
}
