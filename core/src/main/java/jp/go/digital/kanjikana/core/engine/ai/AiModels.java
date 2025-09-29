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

package jp.go.digital.kanjikana.core.engine.ai;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * pythonで計算したモデルを保持する抽象クラス
 */
public abstract class AiModels {
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

    private  WordIndex vocab_src;
    private  WordIndex vocab_tgt;
    private  Map<String, String> params; // ai model output parameter

    private  ZooModel<NDList, NDList> encoder;
    private  ZooModel<NDList, NDList> decoder;
    private  ZooModel<NDList, NDList> positional_encoding;
    private  ZooModel<NDList, NDList> generator;
    private  ZooModel<NDList, NDList> src_tok_emb;
    private  ZooModel<NDList, NDList> tgt_tok_emb;

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
        //try {
            this.vocab_src = new WordIndex(getClass().getResourceAsStream(vocab_src));
            this.vocab_tgt = new WordIndex(getClass().getResourceAsStream(vocab_tgt));
            Parameters dl = new Parameters();
            this.params = dl.load_param(params);

            this.encoder = loadModelFromResource(encoder);
            this.decoder = loadModelFromResource(decoder);
            this.positional_encoding = loadModelFromResource(positional_encoding);
            this.generator = loadModelFromResource(generator);
            this.src_tok_emb = loadModelFromResource(src_tok_emb);
            this.tgt_tok_emb = loadModelFromResource(tgt_tok_emb);
        //}catch(Exception e){
        //    e.fillInStackTrace();
        //    logger.fatal(e);
        //}
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
        URL u = getClass().getResource(path);
        String url = u.getPath();
        //logger.debug("url;" + url); // file:/path/to/kanjikana/target/kanjikana-1.0.jar!/ai/encoder.pt

        File tempfile = null;
        if (url.contains("!")) {
            String fpath = url.split("!")[0];
            tempfile = File.createTempFile("model_", ".tmp");
            Path temppath = tempfile.toPath();
            tempfile.deleteOnExit();

            // https://docs.oracle.com/javase/jp/7/technotes/guides/io/fsp/zipfilesystemprovider.html
            // https://note.com/tkhm_dev/n/ne1df8f3fe5f6
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            // locate file system by using the syntax
            // defined in java.net.JarURLConnection
            URI uri = URI.create("jar:" + fpath);
            try (FileSystem jarfs = FileSystems.newFileSystem(uri, env)) {
                Path pathInJarfile = jarfs.getPath(path);
                Files.copy(pathInJarfile, temppath, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                e.fillInStackTrace();
                throw e;
            }
            url = temppath.toAbsolutePath().toString();
        }

        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelUrls(url)
                        .optModelName(path)
                        .optEngine(Engine.getDefaultEngineName())
                        .optDevice(Device.cpu())
                        .build();

        return criteria.loadModel();
    }
}
