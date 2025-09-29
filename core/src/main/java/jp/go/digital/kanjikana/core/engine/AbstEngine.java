/*
 * MIT License
 *
 * Copyright (c) 2025 デジタル庁
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
import jp.go.digital.kanjikana.core.utils.Moji;

import java.util.ArrayList;
import java.util.List;

abstract class AbstEngine implements EngineIF{
    protected List<DictIF> omitInvalidDict(List<DictIF> dicts){
        List<DictIF> res = new ArrayList<>();
        for(DictIF dict:dicts){
            if(dict.getMaxValLen()>0){
                res.add(dict);
            }
        }
        return res;
    }

    protected String norm_string(String s, DictIF dic){
        if(dic.isNormalized()){
            return Moji.normalize(s);
        }
        return s;
    }


    /**
     * 漢字内に異体字があれば置き換える
     * @param kanji_part  漢字姓名
     * @param idic 異体字辞書
     * @return  異体字を置き換えた漢字姓名
     * @since 1.7
     */
    protected String replace_itaiji(String kanji_part, DictIF idic){

        StringBuilder sb = new StringBuilder();
        // 漢字側を一つずつ異体字に置き換える
        for(int i = 0; i<kanji_part.length();i++) {
            String moji = kanji_part.substring(i, i + 1);
            if (idic.containsKey(moji)) {
                List<String> itaiji_list = idic.getValue(moji); // 必ず1文字
                String s =itaiji_list.get(0);
                sb.append(s);
            }else{
                sb.append(moji);
            }
        }
        return sb.toString();
    }
}
