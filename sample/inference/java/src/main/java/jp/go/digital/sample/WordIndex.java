package jp.go.digital.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 文字と数値の変換
 * 文字は数値として内部では持っている。そのため，入力する文字列は全て数値へ置き換えないとAIで計算できない
 */
public final class WordIndex {
    private static final Logger logger = LogManager.getLogger(WordIndex.class);

    private final Map<String, Long> word2index = new HashMap<>();
    private final Map<Long, String> index2word = new HashMap<>();

    /**
     * 文字と数値を変換するクラスのコンストラクタ
     * @throws Exception 一般的なエラー
     */
    public WordIndex(String fname) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(fname));
        long i = 0;
        for (String line; (line = br.readLine()) != null; ) {
            word2index.put(line, i);
            index2word.put(i, line);
            i++;
        }
        logger.debug("word2index;"+word2index.size());
        logger.debug("index2word;"+index2word.size());
    }

    /**
     * 文字から数値へ変換するMapを返却
     * @return 文字から数値へ返却するMap
     */
    public Map<String ,Long> getWord2Index(){
        return word2index;
    }

    /**
     * 数値から文字へ変換するMapを返却
     * @return 数値から文字へ変換するMap
     */
    public Map<Long, String> getIndex2Word(){
        return index2word;
    }
}
