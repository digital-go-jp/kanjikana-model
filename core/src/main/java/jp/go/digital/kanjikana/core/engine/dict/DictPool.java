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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.ResultAttr;
import jp.go.digital.kanjikana.core.utils.Moji;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 辞書をキャッシュするクラス
 */
public class DictPool {
    private static DictPool dictPool=null;
    private final Map<String, Map<String, Map<String, ResultAttr>>> pool;

    private DictPool(Map<String, Map<String, Map<String, ResultAttr>>> pool){
        this.pool = pool;
    }
    public static synchronized DictPool newInstance(){
        if(dictPool!=null){
            return dictPool;
        }

        Map<String, Map<String, Map<String, ResultAttr>>> pool=new HashMap<>();
        // リソースとして読み込み
        String resource = Resources.getProperty(Resources.PropKey.DIC_POOL);
        try (InputStream is = DictPool.class.getResourceAsStream(resource)) {
            if (is == null) {
                throw new IOException("Resource not found," + Resources.getProperty(Resources.PropKey.DIC_POOL));
            }

            try (ObjectInputStream ois = new ObjectInputStream(is)) {
                pool = (Map<String, Map<String, Map<String, ResultAttr>>>) ois.readObject();
            }
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        dictPool=new DictPool(pool);
        return dictPool;
    }

    private JsonNode getNode(String resource) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(DictPool.class.getResourceAsStream(resource));
        return node;
    }


    static String getMapKey(String resource, boolean normalized){
        return resource+"_"+normalized;
    }

    /**
     * リソース名から辞書を読み込んで返す
     * @param resource リソース名
     * @param normalized 正規化するかどうか　    小書き文字を大書文字へ変換と全銀協で使用できない文字を変換　をやるかどうか　Moji.normalizeで定義
     * @return 辞書データ
     * @throws Exception 一般的なエラー
     */
    //static synchronized Map<String, Map<String, ResultAttr>> load(String resource, boolean normalized) throws Exception {
    //    return load(resource, normalized, 0);
    //}

    /**
     * リソース名から辞書を読み込んで返す
     * @param resource リソース名
     * @param normalized 正規化するかどうか　    小書き文字を大書文字へ変換と全銀協で使用できない文字を変換　をやるかどうか　Moji.normalizeで定義
     * @param min_freq 辞書内で定義された頻度の最小値　漢字とカナのペアとして頻度を辞書内に定義しているが，この値以下ものは漢字とカナのペアを無視する
     * @return 辞書データ
     * @throws Exception 一般的なエラー
     */
     synchronized Map<String, Map<String, ResultAttr>> load(String resource, boolean normalized, int min_freq) throws Exception {
        String poolkey = getMapKey(resource, normalized);
        if(pool.containsKey(poolkey)){
            return pool.get(poolkey);
        }

        //ObjectMapper mapper = new ObjectMapper();
        JsonNode node = getNode( resource);
        Map<String, Map<String, ResultAttr>>  dict =  load(node, normalized, min_freq);
        pool.put(poolkey, dict);
        return dict;
    }


    static synchronized Map<String, Map<String, ResultAttr>> load(JsonNode node, boolean normalized, int min_freq) throws Exception{


        Map<String, Map<String, ResultAttr>> dict =new HashMap<>();

        List<String> keys = keys(node);
        for(String key:keys){  // key は漢字
            JsonNode valNode = node.get(key);
            List<String> vals= keys(valNode);

            Map<String, ResultAttr> valMap=new HashMap<>();

            String normkey= Moji.normalize_basic(key);

            if(normalized){
                normkey = Moji.normalize(key);
            }
            if(dict.containsKey(normkey)){
                valMap=dict.get(normkey);
            }
            for(String val:vals){ // valはかな
                JsonNode attrNode = valNode.get(val);
                if(attrNode==null){
                    System.out.println(val);
                }
                int freq =0;
                if (attrNode.has("freq")) {
                    freq = attrNode.get("freq").intValue();
                    if (freq < min_freq) {
                        continue;
                    }
                }
                List<String> dicNames= new ArrayList<>();
                for(JsonNode dicNode: attrNode.get("dics")){
                    dicNames.add(dicNode.textValue());
                }

                ResultAttr resultAttr = new ResultAttr(freq, dicNames);
                String normval = Moji.normalize_basic(val);
                if(normalized){
                    normval = Moji.normalize(val);
                }
                valMap.put(normval, resultAttr); // 後勝ち
            }
            if(valMap.size()==0){
                continue;  // カタカナがない場合にはスキップする
            }
            dict.put(normkey, valMap);
        }

        return dict;
    }

    private static List<String> keys(JsonNode node){
        //https://www.baeldung.com/java-jsonnode-get-keys
        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = node.fieldNames();
        iterator.forEachRemaining(e -> keys.add(e));
        return keys;
    }
}
