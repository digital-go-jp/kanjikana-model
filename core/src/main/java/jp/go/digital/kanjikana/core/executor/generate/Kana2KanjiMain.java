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

package jp.go.digital.kanjikana.core.executor.generate;

import jp.go.digital.kanjikana.core.executor.Params;
import jp.go.digital.kanjikana.core.utils.FileReader;
import jp.go.digital.kanjikana.core.engine.ai.SearchResult;
import jp.go.digital.kanjikana.core.utils.FileWriter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * AIを用いて，カナから漢字の候補を作成する実行クラス
 * バッチ処理で行い，ログファイルに出力する
 * 
 * <p>実行方法</p>
 * 
 * <pre>{@code
 * java -Xmx4096M -Dlog4j.configurationFile=path/to/log4j2.xml -classpath path/to/jarfile jp.go.digital.kanjikana.core.executor.generate.Kana2KanjiMain  --infile path/to/inputfile --n_best number_ge_1
 * }
 * </pre>
 * 
 * <p>オプション</p>
 *
 * <pre>{@code
 *  -Xmx4096M \   4G以上のメモリが必要
 *  -Dlog4j.configurationFile=path/to/log4j2.xml \ 必要ならばlog4jの定義ファイル
 *  -classpath path/to/jarfile \ jarfileまでのパス
 * jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain \ このクラス
 *  --infile path/to/inputfile \ 入力ファイル　１つのカナ姓名が１行に格納されている，複数行可
 *  --n_best number_ge_1 \ 漢字姓名の推論結果を幾つ出力するか
 *  }
 *  </pre>
 */
public class Kana2KanjiMain {
    private static final Logger logger = LogManager.getLogger(Kana2KanjiMain.class);

    public static void main(String[] args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newFor("kanjikana").build()
                .defaultHelp(true)
                .description("カナから漢字を推測");

        parser.addArgument("--infile").setDefault("input.txt").help("入力するファイル名");
        parser.addArgument("--n_best").setDefault(5).help("出力数");
        parser.addArgument("--kana_idx").setDefault(2).help("カタカナ表記のフィールド番号");
        parser.addArgument("--sep").choices("csv","tsv").setDefault("csv").help("入力行のセパレータ");
        parser.addArgument("--has_header").type(Boolean.class).setDefault(true).help("ヘッダがあるかどうか");
        parser.addArgument("--outfile").setDefault("output.txt").help("出力ファイル名");

        Namespace ns = parser.parseArgs(args);

        String input = ns.getString("infile");
        String outfile = ns.getString("outfile");
        int n_best= Integer.parseInt(ns.getString("n_best"));
        int kana_idx= Integer.parseInt(ns.getString("kana_idx"));
        String sep = ns.getString("sep");
        Params.Separator separator = Params.Separator.TSV;
        if(sep.equals("csv")){
            separator = Params.Separator.CSV;
        }
        boolean has_header = ns.getBoolean("has_header");

        FileReader reader = new FileReader(has_header);
        List<String> lines = reader.getFileText(input);
        String header=null;
        if(has_header){
            header = reader.getHeader(input);
        }
        Params params = new Params(header,-1,kana_idx,separator,lines );


        List<String> output = new ArrayList<>();
        Kana2Kanji kk = new Kana2Kanji();
        for(String line:lines) {
            logger.info(line);
            String kana = params.getKana(line);

            int idx=0;
            List<SearchResult> res = kk.run(kana, n_best);
            for (SearchResult r : res) {
                output.add(line+",kana;"+kana+";best"+(++idx)+";"+r.toString());
            }
        }

        FileWriter o = new FileWriter(outfile);
        o.write(output);
    }
}
