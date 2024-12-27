package jp.go.digital.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * モデルで読み込むデータファイルをプロパティで一元管理
 */
public final class Resources {
    private static final Logger logger = LogManager.getLogger(Resources.class);

    private static final String INIT_FILE_PATH= "/kanjikana.properties";
    private static final Properties properties;
    static{
        properties = new Properties();
        try {
            properties.load(Resources.class.getResourceAsStream(INIT_FILE_PATH));
        } catch (IOException e) {
            // ファイル読み込みに失敗
            logger.fatal(e);
            logger.fatal(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", INIT_FILE_PATH));
        }
    }

    // kanjikana.propのキーを定義
    public enum PropKey{
        AI_DECODER("AI_DECODER"),
        AI_ENCODER("AI_ENCODER"),
        AI_GENERATOR("AI_GENERATOR"),
        AI_PARAMS("AI_PARAMS"),
        AI_POSENC("AI_POSENC"),
        AI_SRCEMB("AI_SRCEMB"),
        AI_TGTEMB("AI_TGTEMB"),
        AI_SRCVOCAB("AI_SRCVOCAB"),
        AI_TGTVOCAB("AI_TGTVOCAB");

        private final String val;
        PropKey(String val){
            this.val = val;
        }
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @return 値
     */
    public static String getProperty(final PropKey key) {
        return properties.getProperty(key.val);
    }
}
