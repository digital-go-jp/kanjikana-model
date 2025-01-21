package jp.go.digital.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * AIを用いて漢字からカナの候補を作成するクラス
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private final SearchParam search_param;
    private final AiModels models;

    public Main(String vocab_src, String vocab_tgt, String positional_encoding, String params, String encoder, String decoder, String generator, String src_tok_emb, String tgt_tok_emb, int n_best,int beam_width,int max_len) throws Exception{
        search_param = new SearchParam(max_len,beam_width,n_best);
        models = new AiModels(vocab_src, vocab_tgt, positional_encoding, params, encoder, decoder, generator, src_tok_emb, tgt_tok_emb);
    }
    /**
     *
     * @throws Exception 一般的なエラー
     */
    public void run(String test_file, String out_file,String search_type) throws Exception {
        List<EngFra> lines = fileReader(new File(test_file));
        boolean first=true;
        for(EngFra ef : lines){
            List<SearchResult> lst = run_line(ef.eng, ef.fra,search_type);
            int i=0;
            for(SearchResult r : lst ){
                String pred = r.getPredict();
                String prob = String.valueOf(r.getProbability());
                String src = ef.eng;
                String tgt = ef.fra;
                String no = "beam"+String.valueOf(i);
                if(search_type.equals("greedy")){
                    no = "greedy";
                }
                String l = no+","+src+","+tgt+","+pred+","+prob;


                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_file, !first), StandardCharsets.UTF_8));
                bw.write(l+"\n");
                bw.close();
                first=false;
                i+=1;
            }
        }

    }
    public List<SearchResult> run_line(String eng, String fra,String search_type) throws Exception{
        Search search;
        if (search_type.equals("beam")){
            search=new BeamSearch(models,search_param);
        }else{
            search = new GreedySearch(models,search_param);
        }
        List<SearchResult> res = search.run(eng);
        search.close();
        return res;
    }

    private static class EngFra{
        public String eng;
        public String fra;
    }
    private static class Jsonl{
        public EngFra translation;
    }


    private EngFra jsonParser(String line) throws Exception{
        List<String> res = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Jsonl node = mapper.readValue(line, Jsonl.class);
        return node.translation;
    }
    private List<EngFra> fileReader(File file) throws Exception{
        List<EngFra> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        );
        String text;
        while ((text = br.readLine()) != null) {
            lines.add(jsonParser(text.trim()));
        }
        br.close();
        return lines;
    }


    public static void main(String[] args) throws Exception{
        ArgumentParser parser = ArgumentParsers.newFor("sample").build().defaultHelp(true).description("sample");

        parser.addArgument("--test_file").setDefault("../dataset/test.jsonl");


        parser.addArgument("--model_script").setDefault("../training/model/script.pt").type(String.class);
        parser.addArgument("--model_encoder").setDefault("../training/model/encoder.pt").type(String.class);
        parser.addArgument("--model_decoder").setDefault("../training/model/decoder.pt").type(String.class);
        parser.addArgument("--model_positional_encoding").setDefault("../training/model/positional_encoding.pt").type(String.class);
        parser.addArgument("--model_generator").setDefault("../training/model/generator.pt").type(String.class);
        parser.addArgument("--model_src_tok_emb").setDefault("../training/model/src_tok_emb.pt").type(String.class);
        parser.addArgument("--model_tgt_tok_emb").setDefault("../training/model/tgt_tok_emb.pt").type(String.class);
        parser.addArgument("--model_vocab_src").setDefault("../training/model/vocab_src.txt").type(String.class);
        parser.addArgument("--model_vocab_tgt").setDefault("../training/model/vocab_tgt.txt").type(String.class);
        parser.addArgument("--model_params").setDefault("../training/model/params.json").type(String.class);

        parser.addArgument("--out_file").setDefault("output.txt");
        parser.addArgument("--n_best").setDefault(5).type(Integer.class);
        parser.addArgument("--beam_width").setDefault(5).type(Integer.class);
        parser.addArgument("--max_len").setDefault(100).type(Integer.class);
        parser.addArgument("--search_type").choices("beam","greedy").setDefault("beam");


        Namespace ns = parser.parseArgs(args);

        String test_file = ns.getString("test_file");
        String out_file = ns.getString("out_file");
        String search_type = ns.getString("search_type");
        int n_best = ns.getInt("n_best");
        int beam_width = ns.getInt("beam_width");
        int max_len = ns.getInt("max_len");

        String vocab_src = ns.getString("model_vocab_src");
        String vocab_tgt= ns.getString("model_vocab_tgt");
        String positional_encoding= ns.getString("model_positional_encoding");
        String params= ns.getString("model_params");
        String encoder= ns.getString("model_encoder");
        String decoder= ns.getString("model_decoder");
        String generator= ns.getString("model_generator");
        String src_tok_emb= ns.getString("model_src_tok_emb");
        String tgt_tok_emb= ns.getString("model_tgt_tok_emb");

        Main obj = new Main(vocab_src, vocab_tgt, positional_encoding, params, encoder, decoder, generator, src_tok_emb, tgt_tok_emb,n_best,beam_width,max_len);
        obj.run(test_file,out_file,search_type);

    }
}
