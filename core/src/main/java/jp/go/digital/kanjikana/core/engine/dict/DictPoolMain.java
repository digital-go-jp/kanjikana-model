package jp.go.digital.kanjikana.core.engine.dict;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.ResultAttr;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;

public class DictPoolMain {
    private final Map<String, Map<String, Map<String, ResultAttr>>> pool=new Hashtable<>();
    private final String dir;
    private DictPoolMain(String dir){
        this.dir = dir;
    }

    private JsonNode getNode(String path) throws Exception{

        ObjectMapper mapper = new ObjectMapper();

        // ファイルパスを指定（絶対パスまたは相対パス）
        File jsonFile = new File(path);
        JsonNode node = mapper.readTree(jsonFile);
        return node;
    }

    private void loadDic(String resource, boolean normalized) throws Exception{
        String poolkey=DictPool.getMapKey(resource,normalized);
        System.out.println("load,"+poolkey);
        JsonNode node = getNode(dir+resource);
        Map<String, Map<String, ResultAttr>>  dict = DictPool.load(node, normalized,0);
        pool.put(poolkey, dict);
    }

    /**
     * あらかじめ辞書をロードしておき，シリアライズして保存する
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{

        ArgumentParser parser = ArgumentParsers.newFor("dictpool").build().defaultHelp(true).description("漢字カナ突合辞書変換プログラム");
        String file= Resources.getProperty(Resources.PropKey.DIC_POOL);
        String basename=new File(file).getName();
        parser.addArgument("--outfile").setDefault("src/main/resources/"+basename).help("出力ファイル");
        parser.addArgument("--resource_dir").setDefault("src/main/resources").help("辞書のあるディレクトリ");



        Namespace ns = parser.parseArgs(args);
        String dir = ns.getString("resource_dir");

        DictPoolMain obj = new DictPoolMain(dir);

        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_STATISTICS),true);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_STATISTICS),false);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_RELIABLE),true);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_RELIABLE),false);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_UNRELIABLE),true);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_UNRELIABLE),false);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_TANKANJI),true);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_TANKANJI),false);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_ITAIJI),true);
        obj.loadDic(Resources.getProperty(Resources.PropKey.DIC_ITAIJI),false);

        String outfile = ns.getString("outfile");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outfile))) {
            oos.writeObject(obj.pool);
        }
    }

}
