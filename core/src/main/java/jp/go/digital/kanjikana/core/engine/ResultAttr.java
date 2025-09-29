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

package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.engine.dict.DictIF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 辞書の1レコードを保持する。統計情報と，辞書の由来を
 */
public final class ResultAttr implements Serializable {

    private  final int freq;
    private  final List<String> dictTypes;

    private String remarks;

    public ResultAttr(){
        this("");
    }

    /**
     * 
     * @param remarks　判定結果の備考
     */
    public ResultAttr(String remarks){
        this.remarks = remarks;
        this.freq = 0;
        this.dictTypes = new ArrayList<>();
    }

    /**
     *
     * @param freq 統計情報，実際に何個あったか
     * @param dictNames 辞書の名前
     */
    public ResultAttr(int freq, List<String> dictNames) {
        this.freq = freq;

        this.dictTypes = new ArrayList<>();
        for(String dictName:dictNames){
            dictTypes.add(dictName);
        }
    }

    public int getFreq() {
        return freq;
    }

    public List<String> getDictNames() {
        return dictTypes;
    }

    public String toText(){
        StringBuilder sb = new StringBuilder();

        sb.append(freq);
        sb.append(":/");
        List<String> names = new ArrayList<>();
        for(String dic :dictTypes) {
            names.add(dic);
        }
        names.sort(Comparator.naturalOrder());
        for(String name:names){
            sb.append(name);
            sb.append("/");
        }
        sb.append(";");
        sb.append(remarks);
        return sb.toString();
    }

}
