package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaiji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaijiDummy;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictReliableNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * キラキラネーム対応。法務省 2025/02/17  発表
 *
 * 心愛　ここあ
 * 彩夢　ゆめ
 * 桜良　さら
 * 美空　そら
 * 飛鳥　あすか
 *
 * 漢字の読みの一部が入る名や置き字，熟字訓
 */
public class NandokuEngine extends AbstEngine{
    private static final Logger logger = LogManager.getLogger(NandokuEngine.class);

    private final List<DictIF> dics;
    private final DictIF idic;
    private final Map<String,YomiList> memo = new HashMap<>(); // 漢字と読みをメモしておく
    private final Map<String, DictData>  dictPool = new HashMap();

    public NandokuEngine(List<DictIF> dics, boolean hasItainji) throws Exception {
        this.dics = super.omitInvalidDict(dics);
        if(hasItainji) {
            this.idic = DictItaiji.newInstance();
        }else{
            this.idic = DictItaijiDummy.newInstance();
        }
    }
    /*
    public NandokuEngine(boolean hasItainji) throws Exception {
        this(Arrays.asList( DictReliableNormalized.newInstance(), DictTankanjiNormalized.newInstance()),hasItainji);
    }*/

    private DictData getDictData(DictIF dict,String dict_kana){
        String key=dict.getClass().getName()+"_"+dict_kana;
        if(!dictPool.containsKey(key)){
            dictPool.put(key,new DictData(dict,dict_kana));
        }
        return dictPool.get(key);
    }

    private static final class DictData{
        private final DictIF dict;
        private final String dict_kana;
        private DictData(DictIF dict, String dict_kana){
            this.dict=dict;
            this.dict_kana=dict_kana;
        }
    }

    /**
     * 漢字の部分文字列とカナの部分文字列とそのペアの由来辞書
     */
    private static final class Yomi{
        private final String kana;  // カナの部分文字列
        private final Map<DictData,String> dic;
        private final String kanji;//  漢字の部分文字列
        private Yomi(String kanji,String kana,Map<DictData,String> dic){
            this.kana=kana;
            this.kanji=kanji;
            this.dic=dic;
        }
        private Yomi(){
            this.kana="";
            this.kanji="";
            dic=new HashMap<>();
        }

        private ResultAttr getAttr(){
            // 由来の辞書を文字列のリストで返す
            List<String> dicnames= getDictData();
            return new ResultAttr(0,dicnames);
        }

        private List<String> getDictData(){
            return dic.keySet().stream().map(c->c.dict.getClass().getSimpleName()).toList();
        }

        @Override
        public String toString(){
            return "kanji;"+kanji+",kana;"+kana+",dic;"+getDictData().toString();
        }
    }

    private static final class YomiList{
        private final List<Yomi> yomi;
        private YomiList(List<Yomi> yomi){
            this.yomi=yomi;
        }
        private YomiList(){
            this.yomi=new ArrayList<>();
        }
    }

    /**
     * 漢字部分文字列のTableでのOriginIdxと読み
     */
    private static final class KanjiIdxKana{
        private int kanji_begin_idx; // 漢字部分文字列の開始位置
        private int kanji_end_idx;  // 漢字部分文字列の終了位置　
        private Yomi yomi;
        private int kana_begin_idx;// オリジナルのカナ文字列でのyomiの開始位置
        private int kana_end_idx; // オリジナルのカナ文字列でのyomiの終了位置
        private KanjiIdxKana(int kanji_begin_idx,int kanji_end_idx,Yomi yomi, int kana_begin_idx,int kana_end_idx){
            this.kanji_begin_idx=kanji_begin_idx;
            this.kanji_end_idx=kanji_end_idx;
            this.yomi=yomi;
            this.kana_begin_idx=kana_begin_idx;
            this.kana_end_idx=kana_end_idx;
        }
        @Override
        public String toString(){
            return "kanji;["+kana_begin_idx+","+kana_end_idx+"],kana;["+kana_begin_idx+","+kana_end_idx+"],yomi;"+yomi.toString();
        }
    }

    private YomiList[][] initTable(String kanji){
        //List<List<List<String>>> table=new ArrayList<>(kanji.length());
        // 漢字名の読みを格納したテーブル　Matrixの行が漢字名始まり位置，Matrixの列が漢字名の終了位置，上三角行列になる。また，行列の要素には，漢字の読みの一覧が入る　例えば　「桃太郎」のtable[0][0]は桃の読みである ['もも','も','とう','と','う']がはいる。辞書の読みで得たものから読みの一部もOKとしている。table[0][2]は太郎の読みがはいるのて['たろう']となる，一文字である　table[i][i]は読みの一部もOKとするが，それ以外の二文字以上は辞書に入ったままの読みとする

        YomiList[][] table=new YomiList[kanji.length()][kanji.length()];

        for(int i=0; i<kanji.length();i++){
            for(int j=i;j<kanji.length();j++){ // 下三角行列はNULLのためこれで良い
                table[i][j]=getDictYomi(kanji.substring(i,j+1));
                if(table[i][j].yomi.isEmpty()){
                    // 異体字チェック
                    table[i][j]=getItaijiConved(kanji.substring(i,j+1),0);
                }
            }
        }
        return table;
    }

    /**
     * 再帰処理で，kanji_partに含まれる全ての痛い痔の組み合わせで検査していく。OKがあればReturn
     * @param kanji_part
     * @param kanji_part_idx
     * @return
     */
    private YomiList getItaijiConved(String kanji_part, int kanji_part_idx) {
        if (kanji_part.length() <= kanji_part_idx) {
            logger.debug("getItaijiConved;"+kanji_part);
            return getDictYomi(kanji_part);
        }

        String prefix = kanji_part.substring(0, kanji_part_idx);
        String suffix = "";
        if (kanji_part_idx + 1 < kanji_part.length()) {
            suffix = kanji_part.substring(kanji_part_idx + 1);
        }

        String ch = kanji_part.substring(kanji_part_idx, kanji_part_idx + 1);
        if (idic.containsKey(ch)) {
            for (String s : idic.getValue(ch)) {
                if (s.equals(ch)) {
                    continue;
                }
                YomiList yo = getItaijiConved(prefix + s + suffix, kanji_part_idx + 1);
                if (!yo.yomi.isEmpty()) {
                    memo.put(kanji_part,yo); // 元の文字にも読みを入れておく
                    return yo;
                }
            }
        } else {
            return getItaijiConved(kanji_part, kanji_part_idx + 1);
        }
        return new YomiList();
    }

    /**
     * 読みを与えて，一文字ずつに分割して，HASHのキーに格納する
     * @param dic_kana　読み
     * @param hsh　Keyに分割した読みの文字列を入れる
     */
    private void setKanaDivision(String dic_kana,Map<String,Map<DictData,String>> hsh,DictIF dic){
        for(int i=0;i<dic_kana.length();i++){
            for(int j=i+1;j<dic_kana.length()+1;j++){
                if(hsh.containsKey(dic_kana.substring(i,j))){ // すでに登録されている
                    hsh.get(dic_kana.substring(i,j)).put(getDictData(dic,dic_kana),"");
                }else{
                    Map<DictData,String> dics = new HashMap<>();
                    dics.put(getDictData(dic,dic_kana),"");
                    hsh.put(dic_kana.substring(i,j),dics);
                }
            }
        }
    }

    /**
     * 辞書を検査し，漢字姓名に対応する読みの一覧を返却する
     * @param kanji_part 漢字姓名の部分文字列
     * @return 読みの一覧
     */
    private YomiList getDictYomi(String kanji_part){
        if(memo.containsKey(kanji_part)){ // メモ化
            return memo.get(kanji_part);
        }
        Map<String,Map<DictData,String>> kanas=new HashMap<>();

        for(DictIF dic:dics){
            if(kanji_part.length()>dic.getMaxKeyLen()){
                continue;
            }
            String kanji_part_copy = norm_string(kanji_part,dic);

            if(dic.containsKey(kanji_part_copy)){
                List<String> res = dic.getValue(kanji_part_copy);
                for(String dic_kana:res) {
                    if(kanji_part_copy.length()==1){
                        // 文字単位にする
                        setKanaDivision(dic_kana,kanas,dic);
                    }else{
                        if(kanas.containsKey(dic_kana)){ // すでにある場合
                            kanas.get(dic_kana).put(getDictData(dic,dic_kana),"");
                        }else{
                            Map<DictData,String> dics=new HashMap<>();
                            dics.put(getDictData(dic,dic_kana),"");
                            kanas.put(dic_kana,dics);
                        }
                    }
                }
            }
        }
        YomiList list = new YomiList();
        for(String kana:kanas.keySet()){
            list.yomi.add(new Yomi(kanji_part,kana,kanas.get(kana)));
        }

        memo.put(kanji_part,list);
        return memo.get(kanji_part);
    }


    /**
     * キラキラ名の読み仮名ロジック　
     * 　１）読みの一部が入る
     * 　３）熟字訓を許容（単漢字では読めない字　「小鳥遊」＝「たかなし」など）
     * @param table　漢字のテーブル
     * @param table_origin_idx　漢字テーブル内での有効な開始行，列の位置
     * @param kana_part　元のカナの部分文字列
     * @param kana_begin_index 元のカナの部分文字列の，現在の位置
     * @param result
     * @return
     */
    private boolean search(YomiList[][] table,int table_origin_idx,String kana_part,int kana_begin_index,List<KanjiIdxKana> result){
        if(table_origin_idx>=table.length && kana_part.length()<=kana_begin_index){
            return true;
        }
        if(table_origin_idx>=table.length || kana_part.length()<=kana_begin_index){
            return false;
        }
        //for(int i=table_origin_idx;i<table.length;i++){
        for(int i=table.length-1;i>=0;i--){ // 長い方から調べる　心愛　心　の順
            if(table[table_origin_idx][i]==null){
                continue;
            }
            for(Yomi yomi:table[table_origin_idx][i].yomi){
                if(kana_part.substring(kana_begin_index).startsWith(yomi.kana)){
                    result.add(new KanjiIdxKana(table_origin_idx, i+1,yomi,kana_begin_index,kana_begin_index+yomi.kana.length()));
                    logger.debug("add;"+result.get(result.size()-1).toString());
                    //if(search(table,i+1, kana_part,kana_begin_index+yomi.kana.length(),result)){ // 見つかったのでorigin_indexを増やす
                    if(search(table,i+1, kana_part,kana_begin_index+yomi.kana.length(),result)){ // 見つかったのでorigin_indexを増やす
                        return true;
                    }else{
                        logger.debug("remove;"+result.get(result.size()-1).toString());
                        result.remove(result.size()-1);
                    }
                }
            }
        }
        // 漢字を一個スキップする
        //result.add(new KanjiIdxKana(table_origin_idx,table_origin_idx+1,new Yomi(),kana_index,kana_index));
        //return search(table,table_origin_idx+1,kana_part,kana_index,result);
        return false;
    }

    /**
     *　キラキラ名ロジック
     * 　２）置き字を許容（読まない漢字）
     *
     * 　まず，漢字姓名をそのままでsearchで検索，NGの場合，一文字づつ漢字姓名から削除し，searchを実行
     * @param table
     * @param table_origin_idx
     * @param kana_part
     * @param kana_begin_index
     * @return
     */
    private List<KanjiIdxKana> search_loop(String kanji_part,YomiList[][] table,int table_origin_idx,String kana_part,int kana_begin_index){
        List<KanjiIdxKana> res=new ArrayList<>();
        List<KanjiIdxKana> result = new ArrayList<>();
        // そのままでチェック
        if(search(table,0,kana_part,0,result)) { // マッチした
            return result;
        }

        YomiList[][] table_omit;
        // 一文字づつ削除していく
        for(int i=0;i<kanji_part.length();i++){
            String kanji_omit=kanji_part.substring(0,i)+kanji_part.substring(i+1,kanji_part.length());
            result = new ArrayList<>();

            int jdx=0;
            table_omit=new YomiList[kanji_omit.length()][kanji_omit.length()];
            for(int j=0;j<table.length;j++){
                if(i==j){
                    continue;
                }
                int kdx=0;
                for(int k=0;k<table.length;k++) {
                    if(i==k){
                        continue;
                    }
                    table_omit[jdx][kdx]=table[j][k];
                    kdx+=1;
                }
                jdx+=1;
            }
            if(search(table_omit,0,kana_part,0,result)) { // マッチした
                return result;
            }
        }
        return null;
    }

    @Override
    public ResultEngineParts check(String kanji_part_orig, String kana_part) throws Exception {
        String kanji_part = replace_itaiji(kanji_part_orig, idic);

        YomiList[][] table=initTable(kanji_part);
        ResultEngineParts res=null ;
        List<KanjiIdxKana> result = search_loop(kanji_part, table,0,kana_part,0);

        if(result!=null){ // マッチした
            for(KanjiIdxKana kk :result){
                String kanji=kanji_part.substring(kk.kanji_begin_idx,kk.kanji_end_idx);
                String kana=kana_part.substring(kk.kana_begin_idx,kk.kana_end_idx);
                ResultEngineParts result_engine=new ResultEngineParts(ResultEngineParts.Type.OK, kanji_part,kk.kanji_begin_idx,kk.kanji_end_idx,kanji,kana_part,kk.kana_begin_idx,kk.kana_end_idx,kana,kk.yomi.getAttr(),this.getClass(),null);
                if(res==null){
                    res=result_engine;
                }else{
                    res.setNextResult(result_engine);
                    result_engine.setPrevResult(res);
                }
            }
            return res;

        }else{
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND, kanji_part_orig,kana_part,new ResultAttr(), this.getClass(), null);
        }
    }

    @Override
    public boolean isValidEngine() {
        return true;
    }
}
