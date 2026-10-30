package jp.go.digital.kanjikana.core.engine.ai.classify;

import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.ZooModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.digital.kanjikana.core.engine.ai.AbstAiEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstClassifyEngine extends AbstAiEngine {
    private static final Logger logger = LogManager.getLogger(AbstClassifyEngine.class);

    private final Map<String, Long> char2id ;
    private final ZooModel<NDList, NDList> classifier;
    private final Map<String, String> params ;

    public Map<String, Long> getChar2id() {return char2id;}
    public Map<String, String> getParams() { return params;}
    public ZooModel<NDList, NDList> getClassifier() {return classifier;}

    private InputStream getStream(String resource_path){
        return getClass().getResourceAsStream(resource_path);
    }
    protected AbstClassifyEngine(String model_scripted,String param_json,String char2id_json) throws Exception{
        this.classifier = loadModelFromResource(model_scripted);
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getStream(char2id_json), StandardCharsets.UTF_8))) {
            this.char2id = mapper.readValue(
                    reader,
                    new TypeReference<Map<String, Long>>() {}
            );
        }
        mapper=new ObjectMapper();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getStream(param_json), StandardCharsets.UTF_8))) {
            this.params = mapper.readValue(
                    reader,
                    new TypeReference<Map<String, String>>() {}
            );
        }
    }
}
