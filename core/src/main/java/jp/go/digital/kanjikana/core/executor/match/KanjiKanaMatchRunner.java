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

package jp.go.digital.kanjikana.core.executor.match;

import jp.go.digital.kanjikana.core.executor.Params;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyOnlyAi;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyOnlyDict;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyOnlyStatistics;
import jp.go.digital.kanjikana.core.utils.FileReader;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.Response;
import jp.go.digital.kanjikana.core.executor.StatusMatch;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyBasic;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyEnsemble;
import jp.go.digital.kanjikana.core.utils.FileWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 漢字カナ突合モデルを実行する
 * スレッドでも実行できるようにRunnableインターフェースを実装
 */
public final class KanjiKanaMatchRunner implements Runnable {
    private static final Logger logger = LogManager.getLogger(KanjiKanaMatchRunner.class);
    /**
     * 引数で取得するために，ストラテジをEnumで保存する
     */
    enum Strategy {
        BASIC("BASIC"),
        ONLY_AI("ONLY_AI"),
        ONLY_DICT("ONLY_DICT"),
        ONLY_STAT("ONLY_STAT"),
        ENSEMBLE("ENSEMBLE")
        ;

        private final String val;

        Strategy(String val) {
            this.val = val;
        }

        /**
         * 文字列形式でストラテジを取得
         * @return ストラテジの文字列表現
         */
        public String getVal() {
            return this.val;
        }

        /**
         * 値に合致する enum 定数を返す。
         */
        public static Strategy getType(String s) {
            // 値から enum 定数を特定して返す処理
            for (Strategy value : Strategy.values()) {
                if (value.getVal().equals(s)) {
                    return value;
                }
            }
            return null; // 特定できない場合
        }
    }

    private final FileWriter outfile;

    private final Params params; // from input

    private final String header;
    private final List<String> lines;
    private final KanjiKanaMatch match;

    /**
     * バッチ実行クラス
     *
     * @param params   入力パラメタクラス
     * @param lines    ヘッダなしのCSVデータ getFileTextで取得したもの
     * @param header   　出力用のヘッダ getHeaderで取得したもの
     * @param outfile   出力ファイル
     * @param strategy basic or advanced 簡易モデルか詳細モデルか
     * @throws Exception 一般的な例外
     */
    public KanjiKanaMatchRunner(Params params, List<String> lines, String header, String outfile,  Strategy strategy) throws Exception {
        FileReader fr = new FileReader(params.hasHeader());
        this.params = params;
        this.outfile = new FileWriter(outfile);

        this.header = header;
        this.lines = lines;
        StrategyIF strategyif;
        if (strategy == Strategy.BASIC) {
            strategyif = StrategyBasic.newInstance();
        } else if (strategy == Strategy.ENSEMBLE) {
            strategyif = StrategyEnsemble.newInstance();
        } else if (strategy == Strategy.ONLY_AI) {
            strategyif = StrategyOnlyAi.newInstance();
        } else if (strategy == Strategy.ONLY_DICT) {
            strategyif = StrategyOnlyDict.newInstance();
        } else if (strategy == Strategy.ONLY_STAT) {
            strategyif = StrategyOnlyStatistics.newInstance();
        } else {
            throw new Exception("strategy is invalid");
        }

        this.match = new KanjiKanaMatch(strategyif);
    }

    private void write(File file, String line, Date stdate, boolean is_append) throws Exception {
        if (file == null) {
            logger.debug(line);
            return;
        }
        //FileWriter fw = new FileWriter(file, is_append);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, is_append), StandardCharsets.UTF_8));

        if (!is_append && line == null) {
            bw.write("");
        } else {
            if (is_append){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
                bw.write(line+","+df.format(stdate)+","+df.format(new Date()) + "\n");
            }else{
                bw.write(line+",start_date,end_date" + "\n");
            }
        }
        bw.close();
    }

    private String[] conv(String s) throws Exception {
        String[] items = s.split(params.getSep());
        String[] ret = new String[2];
        if (params.getSource_idx() >= items.length) {
            throw new Exception("kanji_index is not valid value,kanji_idx;" + params.getSource_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        ret[0] = items[params.getSource_idx()]; // check_params.getKanji_idx()とあわせる
        if (params.getTarget_idx() >= items.length) {
            throw new Exception("kana_index is not valid value,kana_idx;" + params.getTarget_idx() + ",items.len;" + items.length + ",items;" + items[0]);
        }
        ret[1] = items[params.getTarget_idx()]; // check_params.getKana_idx()とあわせる
        return ret;
    }

    @Override
    public void run() {
        long thread_id = Thread.currentThread().getId();
        String line = null;
        try {

            outfile.write(line+",start_date,end_date",false);
            //write(okfile, header, new Date(),false);
            //write(ngfile, header, new Date(),false);

            for (int i = 0; i < this.lines.size(); i++) {
                Date stdate = new Date();
                if (i % 10000 == 0) {
                    logger.debug(thread_id + ",i=" + i);
                }

                line = this.lines.get(i);
                //logfile.write(line+","+df.format(stdate)+","+df.format(new Date()),false);
                //write(logfile, line, stdate, false);
                Output o = null;
                try {
                    String[] items = conv(line);
                    o = match.exec(items[0], items[1]);
                } catch (Exception e) {
                    e.fillInStackTrace();
                    logger.error(e.getStackTrace());
                    logger.error(line);
                    continue;
                }
                //logger.debug("i="+i+",isOk="+o.isOk());
                StatusMatch sm = null;
                if (o.result.getAdditionalProperties().containsKey(OutputMaker.ADDITIONAL_KEY_STATUS)){
                    sm = (StatusMatch)o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS);
                }
                if (o.response == Response.OK && sm!=null && StatusMatch.isOk(sm) ){
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
                    String outline=line+params.getSep() + StatusMatch.isOk(sm) + params.getSep() + sm.toValue() + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES);
                    outline+=","+df.format(stdate)+","+df.format(new Date());
                    outfile.write(outline,true);
                    //write(okfile, line + params.getSep() + StatusMatch.isOk(sm) + params.getSep() + sm.toValue() + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES), stdate,true);
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
                    if(sm!=null) {
                        String outline=line + params.getSep() + StatusMatch.isOk(sm) + params.getSep() + sm.toValue() + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES);
                        outline+=","+df.format(stdate)+","+df.format(new Date());
                        outfile.write(outline,true);
                        //write(ngfile, line + params.getSep() + StatusMatch.isOk(sm) + params.getSep() + sm.toValue() + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES), stdate,true);
                    }else{
                        String outline=line + params.getSep() + false + params.getSep() + StatusMatch.NG + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES);
                        outline+=","+df.format(stdate)+","+df.format(new Date());
                        outfile.write(outline,true);
                        //write(ngfile, line + params.getSep() + false + params.getSep() + StatusMatch.NG + params.getSep() + o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_NOTES), stdate,true);
                    }
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            logger.fatal(e);
            //logger.error(e);
            logger.fatal(line);
        }
    }
}
