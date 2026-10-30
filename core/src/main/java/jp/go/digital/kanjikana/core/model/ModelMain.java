package jp.go.digital.kanjikana.core.model;

import jp.go.digital.kanjikana.core.executor.Params;
import jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchRunner;
import jp.go.digital.kanjikana.core.utils.FileReader;
import jp.go.digital.kanjikana.core.utils.FileWriter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.List;


/**
 * モデル別の重みパラメータ推計用
 * @since v2.0
 */
public class ModelMain {


    public static void main(String[] args) throws Exception{
        ArgumentParser parser = ArgumentParsers.newFor("kanjikana").build().defaultHelp(true).description("漢字カナ突合");

        parser.addArgument("--infile").setDefault("input_2.0o.txt").help("入力CSVファイル");
        parser.addArgument("--outfile").setDefault("data/data.2.0o/output_tmp.txt").help("出力CSVファイル");
        parser.addArgument("--kanji_idx").setDefault(0).help("漢字，アルファベット表記のフィールド番号 (先頭を0としたときの)");
        parser.addArgument("--kana_idx").setDefault(1).help("カタカナ表記のフィールド番号");
        parser.addArgument("--sep").choices("csv","tsv").setDefault("csv").help("入力行のセパレータ");
        parser.addArgument("--has_header").type(Boolean.class).setDefault(false).help("ヘッダがあるかどうか");
        parser.addArgument("--model").setDefault("IDictCharModel").help("ModelIFで実装されているモデルクラス名");

        Namespace ns = parser.parseArgs(args);

        String infile = ns.getString("infile");
        String outfile = ns.getString("outfile");
        int kanji_idx= Integer.parseInt(ns.getString("kanji_idx"));
        int kana_idx= Integer.parseInt(ns.getString("kana_idx"));
        String sep = ns.getString("sep");
        boolean has_header = ns.getBoolean("has_header");
        String model = ns.getString("model");

        FileReader fr = new FileReader(has_header);
        List<String> lines = fr.getFileText(infile);
        Params.Separator separator = Params.Separator.TSV;
        if(sep.equals("csv")){
            separator = Params.Separator.CSV;
        }
        String header = fr.getHeader(infile);
        FileWriter fw = new FileWriter(outfile);
        fw.write(header+",result",false);
        Params params = new Params(header,kanji_idx,kana_idx,separator,lines );
        Class<?> clazz = Class.forName("jp.go.digital.kanjikana.core.model.impl."+model);
        ModelIF obj = (ModelIF)clazz.getDeclaredConstructor().newInstance();
        int i=0;
        System.out.println("size="+lines.size());
        for(String line:lines){
            if (i%1000==0){
                System.out.println(i);
            }
            String[] items = line.split(params.getSep());
            String kanji = items[params.getSource_idx()];
            String kana = items[params.getTarget_idx()];
            ModelData modelData = obj.run(new ModelData(kanji, kana));
            String outline=line+","+modelData.isOk();
            fw.write(outline,true);
            i++;
        }



    }
}
