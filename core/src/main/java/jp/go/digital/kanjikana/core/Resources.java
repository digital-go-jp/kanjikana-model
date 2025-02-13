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
    }

    // kanjikana.propのキーを定義
    public enum PropKey{
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

        // 外国人辞書
        FOREIGNER_DIC("FOREIGNER_DIC"),

        // スクレイピングで集めた辞書
        DIC_CRAWL("DIC_CRAWL"),

        // オープンソース辞書から得た辞書
        DIC_OSS("DIC_OSS"),

        // デジ庁で作成した辞書
        DIC_SEIMEI ("DIC_SEIMEI"),

        // オープンソースの異体字辞書
        DIC_ITAIJI("DIC_ITAIJI"),

        // オープンソースの単漢字辞書
        DIC_TANKANJI("DIC_TANKANJI"),

        // デジ庁で作成した統計辞書
        DIC_STATISTICS("DIC_STATISTICS"),
        DIC_STATISTICS_MINFREQ("DIC_STATISTICS_MINFREQ"), // この値未満はNGとする


        SKIP_KANJI_GE_KANA_DIFF("SKIP_KANJI_GE_KANA_DIFF"), // 漢字文字数＞カナ文字数＋SKIP_KANJI_GE_KANA_DIFF の時にループスキップ

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
}
