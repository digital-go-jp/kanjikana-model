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

package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawl;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawlNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaijiDummy;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictItaiji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSS;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSSNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimei;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimeiNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;
import jp.go.digital.kanjikana.core.engine.foreigner.Foreigner;
import jp.go.digital.kanjikana.core.model.impl.AsIsCharModel;
import jp.go.digital.kanjikana.core.model.impl.AsIsWordModel;
import jp.go.digital.kanjikana.core.utils.Moji;

import java.util.Arrays;
import java.util.List;

/**
 * 単語単位でチェック
 */
public class WordEngine extends  AbstEngine{
    private final List<DictIF> dics;
    private final List<DictIF> defaultDics=Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance(), DictOSS.newInstance(), DictOSSNormalized.newInstance(), DictCrawl.newInstance(), DictCrawlNormalized.newInstance(), DictSeimei.newInstance(), DictSeimeiNormalized.newInstance(), DictTankanji.newInstance(), DictTankanjiNormalized.newInstance());;

    private int SKIP_KANJI_GE_KANA_DIFF=0; // 漢字文字数よりもカナ文字数が少ない場合にはスキップするときの，バッファ文字数。　漢字文字数　＞＝　カナ文字数　＋　SKIP_KANJI_GE_KANA_DIFF　で判定

    private int max_key_len;  // 辞書の値の最大単語長さ
    private int max_val_len; // 辞書のキーの最大単語長さ
    private final DictIF idic; // 異体字

    /**
     * 異体字チェックをしない場合が選べるコンストラクタ
     * @param dics　辞書一覧
     * @param hasItainji falseのとき異体字を使わない
     * @throws Exception 一般的なエラー
     */
    public WordEngine(List<DictIF> dics, boolean hasItainji) throws Exception{
        this.dics = omitInvalidDict(dics);
        if(hasItainji) {
            this.idic = DictItaiji.newInstance();
        }else{
            this.idic = DictItaijiDummy.newInstance();
        }
        init();
    }

    /**
     * 異体字チェックをしない場合にはこちら
     * @param hasItainji falseのとき異体字を使わない
     * @throws Exception
     */
    public WordEngine(boolean hasItainji) throws Exception{
        this.dics =omitInvalidDict( defaultDics);
        if(hasItainji) {
            this.idic = DictItaiji.newInstance();
        }else{
            this.idic = DictItaijiDummy.newInstance();
        }
        init();
    }


    private void init(){
        SKIP_KANJI_GE_KANA_DIFF = Integer.parseInt(Resources.getProperty(Resources.PropKey.SKIP_KANJI_GE_KANA_DIFF));

        int max_key_len=0;
        int max_val_len=0;
        for(DictIF dict:dics){
            if(dict instanceof DictAsIs || dict instanceof DictAsIsNormalized){
                continue;
            }
            //if(dict.getMaxKeyLen()>=Integer.MAX_VALUE){ // AsIs辞書は除く
            //    continue;
            //}
            max_key_len=max_key_len>dict.getMaxKeyLen()?max_key_len:dict.getMaxKeyLen();
            max_val_len=max_val_len>dict.getMaxValLen()?max_val_len:dict.getMaxValLen();
        }
        if(max_val_len<=0){ // AsIs辞書のみの時は最大値をいれる
            max_val_len=Integer.MAX_VALUE;
            max_key_len=Integer.MAX_VALUE;
        }
        this.max_key_len = max_key_len;
        this.max_val_len = max_val_len;
    }


    @Override
    public boolean isValidEngine(){
        int max_key_len=0;
        for(DictIF dict:dics) {
            if (dict instanceof DictAsIs || dict instanceof DictAsIsNormalized) {
                continue;
            }
            max_key_len=max_key_len<dict.getMaxValLen()?dict.getMaxKeyLen():max_val_len;
        }
        if(max_key_len==0){
            return false;
        }
        return true;
    }


    /**
     * 単語単位，文字単位，単語の一部などを入力としてマッチング
     * @param kanji_part 田中
     * @param kana_part タナカ
     * @return 結果クラス
     */
    @Override
    public ResultEngineParts check(String kanji_part, String kana_part){
        // for speedup 20241023 漢字文字数よりもカナ文字数が少ない場合にはスキップ。 英語のみはスキップ　TODO
        if(!kanji_part.matches("^[a-zA-Z0-9ａ-ｚＡ-Ｚ０-９　]+$") && kanji_part.length()>kana_part.length()+SKIP_KANJI_GE_KANA_DIFF){
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(), this.getClass(),null);
        }


        if(kanji_part.length()>max_key_len || kana_part.length()>max_val_len){
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(), this.getClass(),null);
        }

        ResultEngineParts result = check_dict(kanji_part, kana_part);
        result.setEngine(this.getClass());
        if(result.getType()== ResultEngineParts.Type.OK){
            return result;
        }
        // ALPHABET check
        if(Foreigner.en_string_check(kanji_part)){
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND, kanji_part,kana_part,new ResultAttr(),this.getClass(), null);
        }

        // 漢字含むかどうか
        if(!kanji_part.matches("^.*\\p{InCjkUnifiedIdeographs}.*$")){
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND, kanji_part,kana_part,new ResultAttr(),this.getClass(), null);
        }

        //  異体字チェック,漢字を一文字ずつずらして異体字に入れ替えて検査
        for(int i=0;i<kanji_part.length();i++){
            ResultEngineParts ires = check_itaiji(kanji_part,i,kana_part);
            ires.setEngine(this.getClass());
            if(ires.getType()== ResultEngineParts.Type.OK){
                return ires;
            }
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(), this.getClass(),null);
    }

    private String norm_string(String s, DictIF dic){
        if(dic.isNormalized()){
            return Moji.normalize(s);
        }
        return s;
    }

    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part 田中
     * @param kana_part タナカ
     * @return
     */
    private ResultEngineParts check_dict(String kanji_part, String kana_part){
        for(DictIF dic:dics){
            if(kanji_part.length()>dic.getMaxKeyLen() || kana_part.length() > dic.getMaxValLen()){
                continue;
            }
            String kanji_part_copy = norm_string(kanji_part,dic);
            String kana_part_copy = norm_string(kana_part, dic);
            if(dic.containsKey(kanji_part_copy)){
                if(dic.containsValueKey(kanji_part_copy,kana_part_copy)){
                    ResultAttr resultAttr = dic.getAttr(kanji_part_copy,kana_part_copy);
                    return new ResultEngineParts(ResultEngineParts.Type.OK, kanji_part,kana_part, resultAttr, this.getClass(), dic.getClass());
                }
            }
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND, kanji_part,kana_part,new ResultAttr(), this.getClass(), null);
    }

    /**
     * 一文字ずつ漢字を異体字があれば置き換えながら，縦方向優先探索で，その度に辞書マッチングを行う
     * @param kanji_part　漢字姓名の単語やその一部
     * @param kanji_idx　kanji_partの文字列ないの位置
     * @param kana_part　カナ姓名の単語やその一部
     * @return
     */
    private ResultEngineParts check_itaiji(String kanji_part, int kanji_idx, String kana_part){

        ResultEngineParts res = check_dict(kanji_part,kana_part);
        res.setEngine(this.getClass());
        if(res.isOk()){
            return res;
        }

        // 漢字側を一つずつ異体字に置き換える
        for(int i = kanji_idx; i<kanji_part.length();i++){
            String moji = kanji_part.substring(i,i+1);
            if(idic.containsKey(moji)){
                List<String> itaiji_list = idic.getValue(moji);
                for(String itaiji:itaiji_list){
                    String kanji_part_itaiji = kanji_part.substring(0,i)+itaiji+kanji_part.substring(i+1);
                    ResultEngineParts result = check_itaiji(kanji_part_itaiji,i+1,kana_part);
                    if(result.getType()== ResultEngineParts.Type.OK){
                        return result;
                    }
                }
            }
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND, kanji_part,kana_part,new ResultAttr(),this.getClass(), null);
    }
}
