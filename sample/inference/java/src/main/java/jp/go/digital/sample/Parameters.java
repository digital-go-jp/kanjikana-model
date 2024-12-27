package jp.go.digital.sample;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Ai用のパラメタを読み込むためのクラス
 */
public final class Parameters {


    private JsonNode getNode(String resource) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);


        JsonNode node = mapper.readTree(new File(resource));
        return node;
    }

    private List<String> keys(JsonNode node){
        //https://www.baeldung.com/java-jsonnode-get-keys
        List<String> keys = new ArrayList<>();
        Iterator<String> iterator = node.fieldNames();
        iterator.forEachRemaining(e -> keys.add(e));
        return keys;
    }


    /**
     * リソース名を与えて，実際のデータをMapとして返す
     * @param resource リソース名, resources以下のファイルを指定
     * @return リソースの中身をMap形式で返却する
     * @throws Exception 一般的なエラー
     */
    Map<String, String> load_param(String resource) throws Exception {
        JsonNode node = getNode(resource);
        Map<String, String> dict =new HashMap<>();
        List<String> keys = keys(node);
        for(String key:keys){  // key は漢字
            JsonNode valNode = node.get(key);
            dict.put(key, valNode.asText());
        }
        return dict;
    }

}
