package jp.go.digital.kanjikana.core.engine.ai;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AbstAiEngine {
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

    /**
     * 漢字とカナをくっつけて入力する際の、漢字とカナのセパレータ
     */
    public static final String SEP="<sep>";

    protected ZooModel<NDList, NDList> loadModelFromResource(String path) throws ModelException, IOException {
        URL u = getClass().getResource(path);
        String url = u.getPath();
        //logger.debug("url;" + url); // file:/path/to/kanjikana/target/kanjikana-1.0.jar!/ai/encoder.pt

        if (url.contains("!")) {
            // Avoid FileSystems.newFileSystem: it throws FileSystemAlreadyExistsException when the jar FS is already open
            File tempfile = File.createTempFile("model_", ".tmp");
            Path temppath = tempfile.toPath();
            tempfile.deleteOnExit();
            try (InputStream in = getClass().getResourceAsStream(path)) {
                if (in == null) {
                    throw new IOException("Resource not found: " + path);
                }
                Files.copy(in, temppath, StandardCopyOption.REPLACE_EXISTING);
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
