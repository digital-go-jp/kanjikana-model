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

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jp.go.digital.kanjikana.core.engine.ResultAttr;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 辞書の抽象クラス
 * implが継承する抽象クラス
 */
public abstract class Dict implements DictIF{
    private final Map<String, Map<String, ResultAttr>> dictMap;
    private final int max_key_len;
    private final int max_val_len;

    private final boolean normalized; // 辞書のカタカナ，漢字を正規化するかどう>か

    /**
     * 辞書作成
     * @param resource resourceファイル名　/で始める
     * @param normalized 辞書のキーと値をそれぞれMojiで正規化するかどうか
     */
    public Dict(String resource, boolean normalized) throws Exception{
        this(resource,normalized,0);
    }

    /**
     * 辞書作成
     * @param resource resourceファイル名　/で始める
     * @param normalized 辞書のキーと値をそれぞれMojiで正規化するかどうか
     * @param min_freq 辞書の漢字とカナのペアの頻度の最小値，この値以下のペアは>無視
     */
    public Dict(String resource, boolean normalized, int min_freq) throws Exception{
        this.normalized = normalized;
        this.dictMap = DictPool.newInstance().load(resource, normalized, min_freq);

        int max_key_len=0;
        int max_val_len=0;
        for(String key: dictMap.keySet()){
            if(key.length()>max_key_len){
                max_key_len=key.length();
            }
            for(String val: dictMap.get(key).keySet()){
                if(val.length()>max_val_len){
                    max_val_len=val.length();
                }
            }
        }
        this.max_key_len = max_key_len;
        this.max_val_len = max_val_len;

    }

    /**
     * ファイルに保存する
     * @param filePath ファイルパス
     * @throws Exception 一般的なエラー
     */
    void save(String filePath) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(filePath), dictMap);
    }

    @Override
    public boolean containsKey(String key) {
        return dictMap.containsKey(key);
    }

    @Override
    public boolean containsValueKey(String key, String valueKey) {
        if(!containsKey(key)){
            return false;
        }
        return dictMap.get(key).containsKey(valueKey);
    }

    @Override
    public List<String> getValue(String key) {
        if(!containsKey(key)){
            return new ArrayList<>();
        }
        return dictMap.get(key).keySet().stream().toList();
    }

    @Override
    public ResultAttr getAttr(String key, String valueKey) {
        if(!containsKey(key)){
            return new ResultAttr();
        }
        return dictMap.get(key).get(valueKey);
    }

    @Override
    public int getMaxKeyLen() {
        return max_key_len;
    }

    @Override
    public int getMaxValLen() {
        return max_val_len;
    }

    @Override
    public boolean isNormalized() {
        return normalized;
    }

}
