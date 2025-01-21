package jp.go.digital.sample;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * pythonで計算したモデルを保持する抽象クラス
 */
public class AiModels {
    private static final Logger logger = LogManager.getLogger(AiModels.class);

    /**
     * 未知の文字の時にはこれが入る
     */
    public static final String UNK = "<unk>";
    /**
     * バッチ処理などで埋めるための文字が必要な時はこれが入る
     */
    public static final String PAD = "<pad>";
    /**
     * 入力する文字列の最初にこれを入れておく
     */
    public static final String BOS = "<bos>";
    /**
     * 入力する文字列の最後にこれを入れておく
     */
    public static final String EOS = "<eos>";

    private final WordIndex vocab_src;
    private final WordIndex vocab_tgt;
    private final Map<String, String> params; // ai model output parameter

    private final ZooModel<NDList, NDList> encoder;
    private final ZooModel<NDList, NDList> decoder;
    private final ZooModel<NDList, NDList> positional_encoding;
    private final ZooModel<NDList, NDList> generator;
    private final ZooModel<NDList, NDList> src_tok_emb;
    private final ZooModel<NDList, NDList> tgt_tok_emb;

    public WordIndex getVocab_src() {
        return vocab_src;
    }

    public WordIndex getVocab_tgt() {
        return vocab_tgt;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public ZooModel<NDList, NDList> getEncoder() {
        return encoder;
    }

    public ZooModel<NDList, NDList> getDecoder() {
        return decoder;
    }

    public ZooModel<NDList, NDList> getPositional_encoding() {
        return positional_encoding;
    }

    public ZooModel<NDList, NDList> getGenerator() {
        return generator;
    }

    public ZooModel<NDList, NDList> getSrc_tok_emb() {
        return src_tok_emb;
    }

    public ZooModel<NDList, NDList> getTgt_tok_emb() {
        return tgt_tok_emb;
    }



    /**
     * コンストラクタ
     * @param vocab_src パラメタリソース名
     * @param vocab_tgt パラメタリソース名
     * @param positional_encoding パラメタリソース名
     * @param params パラメタリソース名
     * @param encoder パラメタリソース名
     * @param decoder パラメタリソース名
     * @param generator パラメタリソース名
     * @param src_tok_emb パラメタリソース名
     * @param tgt_tok_emb パラメタリソース名
     */
    protected AiModels(String vocab_src, String vocab_tgt, String positional_encoding, String params, String encoder, String decoder, String generator, String src_tok_emb, String tgt_tok_emb) throws Exception{

            this.vocab_src = new WordIndex(vocab_src);
            this.vocab_tgt = new WordIndex(vocab_tgt);
            Parameters dl = new Parameters();
            this.params = dl.load_param(params);

            this.encoder = loadModelFromResource(encoder);
            this.decoder = loadModelFromResource(decoder);
            this.positional_encoding = loadModelFromResource(positional_encoding);
            this.generator = loadModelFromResource(generator);
            this.src_tok_emb = loadModelFromResource(src_tok_emb);
            this.tgt_tok_emb = loadModelFromResource(tgt_tok_emb);

    }

    /**
     * パラメタを閉じる
     */
    public void close(){
        this.encoder.close();
        this.decoder.close();
        this.positional_encoding.close();
        this.generator.close();
        this.src_tok_emb.close();
        this.tgt_tok_emb.close();
    }

    private ZooModel<NDList, NDList> loadModelFromResource(String path) throws ModelException, IOException {
        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelPath(Paths.get(path))
                        .optModelName(path)
                        .optEngine(Engine.getDefaultEngineName())
                        .optDevice(Device.cpu())
                        .build();

        return criteria.loadModel();
    }
}
