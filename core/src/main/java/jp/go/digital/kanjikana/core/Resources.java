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

package jp.go.digital.kanjikana.core;

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
        //System.out.println(properties);
        //System.out.println("---");
    }

    // kanjikana.propのキーを定義
    public enum PropKey{

        // 漢字とかなの一致を２値分類するモデル since v2.0
        AI_CLASSIFY_E("AI_CLASSIFY_E"),
        AI_CLASSIFY_E_PARAM("AI_CLASSIFY_E_PARAM"),
        AI_CLASSIFY_E_VOCAB("AI_CLASSIFY_E_VOCAB"),
        // 漢字とかなの一致を２値分類するモデル since v2.0
        AI_CLASSIFY_J("AI_CLASSIFY_J"),
        AI_CLASSIFY_J_PARAM("AI_CLASSIFY_J_PARAM"),
        AI_CLASSIFY_J_VOCAB("AI_CLASSIFY_J_VOCAB"),

        // 漢字からカナを推測するAI
        AI_DECODER("AI_DECODER"),
        AI_ENCODER("AI_ENCODER"),
        AI_GENERATOR("AI_GENERATOR"),
        AI_PARAMS("AI_PARAMS"),
        AI_POSENC("AI_POSENC"),
        AI_SRCEMB("AI_SRCEMB"),
        AI_TGTEMB("AI_TGTEMB"),
        AI_SRCVOCAB("AI_SRCVOCAB"),
        AI_TGTVOCAB("AI_TGTVOCAB"),

        // カナから漢字を推論するAI
        AI_R_DECODER("AI_R_DECODER"),
        AI_R_ENCODER("AI_R_ENCODER"),
        AI_R_GENERATOR("AI_R_GENERATOR"),
        AI_R_PARAMS("AI_R_PARAMS"),
        AI_R_POSENC("AI_R_POSENC"),
        AI_R_SRCEMB("AI_R_SRCEMB"),
        AI_R_TGTEMB("AI_R_TGTEMB"),
        AI_R_SRCVOCAB("AI_R_SRCVOCAB"),
        AI_R_TGTVOCAB("AI_R_TGTVOCAB"),

        // 漢字からカナを推測，カナから漢字を推測のAIで用いるパラメタ
        AI_PM_NBEST("AI_PM_NBEST"),  // 最良から何個取得するか
        AI_PM_BEAMWIDTH("AI_PM_BEAMWIDTH"), // ビームサーチ幅
        AI_PM_MAXLEN("AI_PM_MAXLEN"), // 推測する文字列の最大長さ
        AI_PM_CACHE_SIZE("AI_PM_CACHE_SIZE"), // AIモデルでCacheするサイズ，
        AI_PM_PARALLEL_ENABLED("AI_PM_PARALLEL_ENABLED"),
        AI_PM_PARALLELISM("AI_PM_PARALLELISM"),

        // 外国人辞書
        FOREIGNER_DIC("FOREIGNER_DIC"),

        // JLIS照会辞書,OSS辞書,スクレイピング辞書のマージした辞書
        DIC_RELIABLE("DIC_RELIABLE"),

        // デジ庁で作成した辞書やWikipediaから抜き出した辞書など，信頼性に劣るもの
        DIC_UNRELIABLE("DIC_UNRELIABLE"),

        // オープンソースの異体字辞書
        DIC_ITAIJI("DIC_ITAIJI"),

        // オープンソースの単漢字辞書
        DIC_TANKANJI("DIC_TANKANJI"),

        // デジ庁で作成した統計辞書
        DIC_STATISTICS("DIC_STATISTICS"),
        DIC_STATISTICS_MINFREQ("DIC_STATISTICS_MINFREQ"), // この値未満はNGとする

        DIC_POOL("DIC_POOL"), // DICをキャッシュしておくためのファイル名

        SKIP_KANJI_GE_KANA_DIFF("SKIP_KANJI_GE_KANA_DIFF"), // 漢字文字数＞カナ文字数＋SKIP_KANJI_GE_KANA_DIFF の時にループスキップ

        /*
        MODEL_FWordModel_FWordEngine("MODEL_FWordModel_FWordEngine"),
        MODEL_IReliableWordModel_WordEngine("MODEL_IReliableWordModel_WordEngine"),
        MODEL_INandokuWordModel_WordEngine("MODEL_INandokuWordModel_WordEngine"),
        MODEL_INandokuWordModel_NandokuEngine("MODEL_INandokuWordModel_NandokuEngine"),
        MODEL_IDictCharModel_FWordEngine("MODEL_IDictCharModel_FWordEngine"),
        MODEL_FWordModel_WordEngine("MODEL_FWordModel_WordEngine"),
        MODEL_IDictCharModel_CharEngine("MODEL_IDictCharModel_CharEngine"),
        MODEL_IDictCharModel_WordEngine("MODEL_IDictCharModel_WordEngine"),
        MODEL_FCharModel_FCharEngine("MODEL_FCharModel_FCharEngine"),
        MODEL_IDictWordModel_WordEngine("MODEL_IDictWordModel_WordEngine"),
        MODEL_IDictCharModel_NandokuEngine("MODEL_IDictCharModel_NandokuEngine"),
        MODEL_IDictWordModel_FWordEngine("MODEL_IDictWordModel_FWordEngine"),
        MODEL_FCharModel_FWordEngine("MODEL_FCharModel_FWordEngine"),
        MODEL_Default("MODEL_Default"),  // この中にないモデルでのOK確率、実際には1-MODEL_DefaultでAIモデルでNGの時にかける
        */

        MODEL_AICHAR("MODEL_AICHAR"),
        MODEL_AIWORD("MODEL_AIWORD"),
        MODEL_ASISCHAR("MODEL_ASISCHAR"),
        MODEL_ASISWORD("MODEL_ASISWORD"),
        MODEL_CLASSIFYCHAR("MODEL_CLASSIFYCHAR"),
        MODEL_CLASSIFYWORD("MODEL_CLASSIFYWORD"),

        MODEL_FCHAR("MODEL_FCHAR"),
        MODEL_FWORD("MODEL_FWORD"),
        MDOEL_IAICHAR("MDOEL_IAICHAR"),
        MODEL_IAIWORD("MODEL_IAIWORD"),
        MODEL_ICLASSIFYCHAR("MODEL_ICLASSIFYCHAR"),
        MODEL_ICLASSIFYWORD("MODEL_ICLASSIFYWORD"),
        MODEL_IDICTCHAR("MODEL_IDICTCHAR"),
        MODEL_IDICTWORD("MODEL_IDICTWORD"),
        MODEL_INANDOKUWORD("MODEL_INANDOKUWORD"),
        MODEL_INANDOKUCHAR("MODEL_INANDOKUCHAR"),
        MODEL_IRELIABLEWORD("MODEL_IRELIABLEWORD"),
        MODEL_RELIABLEWORD("MODEL_RELIABLEWORD"),
        MODEL_DEFAULT("MODEL_DEFAULT"),

        // @since v2.0 離散値で評価値を返すためのパラメタ
        DIC_STATISTICS_MAXFREQ("DIC_STATISTICS_MAXFREQ"),

        PROB_MAX("PROB_MAX"),
        PROB_MIN("PROB_MIN"),
        STRATEGY_EnsembleOkUnderLimit("STRATEGY_EnsembleOkUnderLimit"),


        TIMEOUT_SECOND("TIMEOUT_SECOND"), // ver2.0 for timeout
        VERSION("VERSION");

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

    /**
     * モデルの掛け目を取得
     * モデルごとに信頼度が異なるので、モデルででた確率に掛け目を入れて計算する
     */
    public static String getModelProperty(final String key) {
        try {
            String model_key = "MODEL_" + key.replace("Model","").toUpperCase();
            if (properties.containsKey(model_key)){
                return properties.getProperty(model_key);
            }else{
                return properties.getProperty("MODEL_DEFAULT");
            }

            //return properties.getProperty("MODEL_" + key.replace("Model","").toUpperCase());
        }catch(Exception ex) {
            throw new RuntimeException(ex);
            //return properties.getProperty("MODEL_DEFAULT");
        }
    }

    /**
     * "MODEL_FCharModel_FWordEngine" のような文字列から PropKey を取得する。
     * 不正な文字列の場合は null を返す。
     */
    /*
    public static PropKey getPropKey(String model, String engine) {
        if (model == null || model.isBlank() || engine==null || engine.isBlank()) {
            return null;
        }
        String key="MODEL_"+model+"_"+engine;
        try {
            // enum定数名と一致する前提（例: "MODEL_FCharModel_FWordEngine"）
            return PropKey.valueOf(key);
        } catch (IllegalArgumentException ex) {
            return PropKey.valueOf(PropKey.MODEL_DEFAULT.val);
        }
    }*/
}
