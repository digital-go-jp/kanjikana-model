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

import jp.go.digital.kanjikana.core.utils.FileReader;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 漢字カナ突合モデル実行用クラス
 * バッチ処理で使う
 * 
 * 
 * <p>実行方法</p>
 * 
 * <pre>{@code
 * java -Xmx4096M -Dlog4j.configurationFile=path/to/log4j2.xml -classpath path/to/jarfile jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain  --infile path/to/inputfile --okfile path/to/okfile --ngfile path/to/ngfile --logfile path/to/logfile --kanji_idx field_num_to_kanji --kana_idx field_num_to_kana --sep [csv/tsv] --thread_num number_ge_1 --has_header [true/false] --strategy [basic|ensemble/ai]
 * }
 </pre>

 * <p>オプション</p>
 * <pre>{@code
 *  -Xmx4096M \   4G以上のメモリが必要
 *  -Dlog4j.configurationFile=path/to/log4j2.xml \ 必要ならばlog4jの定義ファイル
 *  -classpath path/to/jarfile \ jarfileまでのパス
 * jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain \ このクラス
 *  --infile path/to/inputfile \ 入力ファイル　TSVもしくはCSV形式
 *  --okfile path/to/okfile  \ OKと判定された入力ファイルの結果が，このファイルに結果とともに出力される
 *  --ngfile path/to/ngfile  \ NGと判定された入力ファイルの結果が，このファイルに結果とともに出力される
 *  --logfile path/to/logfile \ 実行ログが出力される 
 *  --kanji_idx field_num_to_kanji \ 入力ファイルのどの列に漢字姓名が含まれるか？　0から始まる
 *  --kana_idx field_num_to_kana \ 出力ファイルのどの列にカナ生命が含まれるか？　0から始まる
 *  --sep [csv/tsv] \ 入力ファイルの形式がTSVかCSVか
 *  --thread_num number_ge_1 \ スレッドに分けて計算するかどうか，１以上の整数。1の時はシングルスレッド。スレッドの分けた時には okfile,ngfileのファイル名に拡張子としてスレッド番号が付与される
 *  --has_header [true/false] \ 入力ファイルにヘッダがあるかどうか？　ヘッダがある場合にはokfile, ngfileにも付与される
 *  --strategy [basic|ensemble/ai] \ どのストラテジで計算実行するか　Basicは簡易モデルで信頼度高い辞書マッチのみ。Ensembleは詳細モデルで，簡易モデルを実行しNGとなったものに対して，デジ庁辞書マッチ，統計マッチ，AIマッチを行い多数決で決定する。AIはAIマッチのみを行う。
 *  }
 * </pre>
 */
public class KanjiKanaMatchMain {
    private static final Logger logger = LogManager.getLogger(KanjiKanaMatchMain.class);

    public static void main(String[] args) throws Exception{

        ArgumentParser parser = ArgumentParsers.newFor("kanjikana").build().defaultHelp(true).description("漢字カナ突合");

        parser.addArgument("--infile").setDefault("input.txt").help("CSVファイル");
        parser.addArgument("--okfile").setDefault("sample_ok.txt").help("CSVファイル");
        parser.addArgument("--ngfile").setDefault("sample_ng.txt").help("CSVファイル");
        parser.addArgument("--logfile").setDefault("sample_log.txt").help("txtファイル");
        parser.addArgument("--kanji_idx").setDefault(1).help("漢字，アルファベット表記のフィールド番号 (先頭を0としたときの)");
        parser.addArgument("--kana_idx").setDefault(2).help("カタカナ表記のフィールド番号");
        parser.addArgument("--sep").choices("csv","tsv").setDefault("csv").help("入力行のセパレータ");
        parser.addArgument("--thread_num").type(Integer.class).setDefault(1).help("スレッド数");
        parser.addArgument("--has_header").type(Boolean.class).setDefault(false).help("ヘッダがあるかどうか");
        parser.addArgument("--strategy").choices(Arrays.asList(KanjiKanaMatchRunner.Strategy.BASIC.getVal(),KanjiKanaMatchRunner.Strategy.ONLY_AI.getVal(), KanjiKanaMatchRunner.Strategy.ONLY_DICT.getVal(),KanjiKanaMatchRunner.Strategy.ONLY_STAT.getVal(),KanjiKanaMatchRunner.Strategy.AI.getVal(),
                KanjiKanaMatchRunner.Strategy.ENSEMBLE.getVal())).setDefault(KanjiKanaMatchRunner.Strategy.ONLY_DICT.getVal()).help("モデル");

        Namespace ns = parser.parseArgs(args);

        String infile = ns.getString("infile");
        String okfile = ns.getString("okfile");
        String ngfile = ns.getString("ngfile");
        String logfile = ns.getString("logfile");
        int kanji_idx= Integer.parseInt(ns.getString("kanji_idx"));
        int kana_idx= Integer.parseInt(ns.getString("kana_idx"));
        String sep = ns.getString("sep");
        boolean has_header = ns.getBoolean("has_header");
        Path currRelativePath = Paths.get("");
        String currAbsolutePathString = currRelativePath.toAbsolutePath().toString();
        int thread_num = ns.getInt("thread_num");
        KanjiKanaMatchRunner.Strategy strategy = KanjiKanaMatchRunner.Strategy.getType(ns.getString("strategy"));

        logger.debug("Current absolute path is - " + currAbsolutePathString);

        FileReader fr = new FileReader(has_header);

        List<String> lines = fr.getFileText(infile);
        Params.Separator separator = Params.Separator.TSV;
        if(sep.equals("csv")){
            separator = Params.Separator.CSV;
        }

        String header = fr.getHeader(infile);

        if(thread_num>1 && lines.size()>thread_num){
            // https://www.techiedelight.com/ja/split-list-into-sub-lists-java/
            // リストをthread_num分割する
            int n= lines.size()/thread_num+1;
            List<List<String>> newlines = ListUtils.partition(lines, n);

            // https://java.keicode.com/lang/multithreading-executor.php
            // thread pool
            ExecutorService pool = Executors.newFixedThreadPool(thread_num);
            for(int i=0;i<thread_num;i++){

                Params params = new Params(has_header, kanji_idx,kana_idx,separator,newlines.get(i) );
                pool.submit(new KanjiKanaMatchRunner(params,newlines.get(i), header,okfile+"."+i, ngfile+"."+i, logfile+"."+i, strategy));
            }
            pool.shutdown();
        }else{
            Params params = new Params(has_header,kanji_idx,kana_idx,separator,lines );
            KanjiKanaMatchRunner ch = new KanjiKanaMatchRunner(params,lines, header,okfile, ngfile, logfile, strategy);
            ch.run();
        }
    }
}
