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

import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawl;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictCrawlNormalized;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSS;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictOSSNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimei;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimeiNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;
import jp.go.digital.kanjikana.core.utils.Moji;

import java.util.Arrays;
import java.util.List;

/**
 * 単語を入力し，文字単位でチェック
 */
public class CharEngine implements  EngineIF{
    //private final List<DictIF> dics = Arrays.asList(DictCrawl.newInstance(), DictCrawlNormalized.newInstance(), DictSeimei.newInstance(), DictSeimeiNormalized.newInstance(), DictTankanji.newInstance(), DictTankanjiNormalized.newInstance());
    private final List<DictIF> defaultDics = Arrays.asList(DictAsIs.newInstance(), DictAsIsNormalized.newInstance(), DictOSS.newInstance(), DictOSSNormalized.newInstance(),DictCrawl.newInstance(), DictCrawlNormalized.newInstance(), DictSeimei.newInstance(), DictSeimeiNormalized.newInstance(), DictTankanji.newInstance(), DictTankanjiNormalized.newInstance());
    private final List<DictIF> dics ;
    private boolean hasItaiji = true;
    private final WordEngine engine;
    //private final List<DictIF> dics;

    /**
     * 異体字を使わない場合にはこちらをつかう
     * @param dics
     * @param hasItaiji falseのとき異体字チェックをしない
     * @throws Exception
     */
    public CharEngine(List<DictIF> dics,boolean hasItaiji) throws Exception{
        this.dics = dics;
        this.hasItaiji = hasItaiji;
        this.engine = new WordEngine(this.hasItaiji);
    }

    /**
     * 異体字を使わない場合にはこちらをつかう
     * @param hasItaiji falseのとき異体字チェックをしない
     * @throws Exception
     */
    public CharEngine(boolean hasItaiji) throws Exception{
        this.dics = defaultDics;
        this.hasItaiji = hasItaiji;
        this.engine = new WordEngine(this.hasItaiji);
    }

    /**
     *　最短マッチで行う。kanji_chars, kana_charsにはスペースが入っている可能性があるので，スペースは削除してマッチングする
     * @param kanji_orig 元の文字列
     * @param kanji_begin_idx kanjiのkanji_origでの開始位置 substringの引数参照
     * @param kanji_end_idx kanjiのkanji_origでの終了位置 substringの引数参照
     * @param kana_orig 元の読み仮名文字列
     * @param kana_begin_idx　kanaのkana_origでの開始位置 substringの引数参照
     * @param kana_end_idx kanaのkana_origでの終了位置 substringの引数参照
     * @return 現在の結果が入っている next==null
     * @deprecated 使用していない
     */
    private ResultEngineParts check_short2long(String kanji_orig, int kanji_begin_idx, int kanji_end_idx, String kana_orig, int kana_begin_idx, int kana_end_idx ) throws Exception{

        //WordEngine engine = new WordEngine(this.dics, this.hasItaiji);
        WordEngine engine = new WordEngine(this.hasItaiji);

        String kanji_chars = kanji_orig.substring(kanji_begin_idx,kanji_end_idx);
        String kana_chars = kana_orig.substring(kana_begin_idx, kana_end_idx);

        if(kanji_chars.length()==0 && kana_chars.length()==0){
            ResultEngineParts res = new ResultEngineParts(ResultEngineParts.Type.OK,"","",new ResultAttr(),this.getClass(), null); // EOD
            return res; // 直近を返す
        }else if(kanji_chars.length()==0 && kana_chars.length()>0 || kanji_chars.length()>0 && kana_chars.length()==0){
            ResultEngineParts res = new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_chars,kana_chars,new ResultAttr(),this.getClass(),null); // ERROR
            return res;
        }

        ResultEngineParts next_result = null;
        ResultEngineParts now_result = null;
        for(int i=1;i<=kanji_chars.length();i++){
            // 次の文字が禁則文字のときにはスキップ
            if(i<kanji_chars.length() && Moji.isKinsoku(kanji_chars.substring(i))){
                continue;
            }

            for(int j=1;j<=kana_chars.length();j++){

                // 次の文字が禁則文字のときにはスキップ
                if(j<kana_chars.length() && Moji.isKinsoku(kana_chars.substring(j))){
                    continue;
                }

                String kanji_tmp = kanji_chars.substring(0,i);
                String kana_tmp = kana_chars.substring(0,j);
                // ここでスペースを削除して検査
                now_result = engine.check(kanji_tmp.replace("　",""),kana_tmp.replace("　",""));
                now_result.setEngine(this.getClass());
                if(now_result.isOk()){
                    now_result.setCharParams(kanji_orig,kanji_begin_idx,kanji_begin_idx+i,kana_orig,kana_begin_idx,kana_begin_idx+j);
                    next_result = check_short2long(kanji_orig,kanji_begin_idx+i,kanji_orig.length(),kana_orig,kana_begin_idx+j,kana_orig.length());
                    next_result.setEngine(this.getClass());

                    // OKもしくは最後まで行った場合には終了
                    if(next_result.isOk() ){
                        if(next_result.getKanji().length()==0 && next_result.getKana().length()==0){
                            return now_result;
                        }
                        next_result.setPrevResult(now_result);
                        now_result.setNextResult(next_result);
                        return now_result;
                    }
                    now_result.setNextResult(null); // 削除する
                }
            }
        }
        if(next_result!=null && now_result.getNextResult()==null){
            now_result.setNextResult(next_result);
            next_result.setPrevResult(now_result);
        }else{
            now_result = new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_chars,kana_chars,new ResultAttr(),this.getClass(),null);
            now_result.setCharParams(kanji_orig,kanji_begin_idx,kanji_end_idx,kana_orig,kana_begin_idx,kana_end_idx);
        }
        return now_result;
    }

    /**
     *　最長マッチで行う。kanji_chars, kana_charsにはスペースが入っている可能性があるので，スペースは削除してマッチングする
     * @param kanji_orig 元の文字列
     * @param kanji_begin_idx kanjiのkanji_origでの開始位置 substringの引数参照
     * @param kanji_end_idx kanjiのkanji_origでの終了位置 substringの引数参照
     * @param kana_orig 元の読み仮名文字列
     * @param kana_begin_idx　kanaのkana_origでの開始位置 substringの引数参照
     * @param kana_end_idx kanaのkana_origでの終了位置 substringの引数参照
     * @return 現在の結果が入っている next==null
     */
    private ResultEngineParts check_long2short(String kanji_orig, int kanji_begin_idx, int kanji_end_idx, String kana_orig, int kana_begin_idx, int kana_end_idx ) throws Exception{
        //WordEngine engine = new WordEngine(this.dics, this.hasItaiji);
        //WordEngine engine = new WordEngine(this.hasItaiji);

        String kanji_chars = kanji_orig.substring(kanji_begin_idx,kanji_end_idx);
        String kana_chars = kana_orig.substring(kana_begin_idx, kana_end_idx);

        if(kanji_chars.length()==0 && kana_chars.length()==0){
            ResultEngineParts res = new ResultEngineParts(ResultEngineParts.Type.OK,"","",new ResultAttr(),this.getClass(), null); // EOD
            return res; // 直近を返す
        }else if(kanji_chars.length()==0 && kana_chars.length()>0 || kanji_chars.length()>0 && kana_chars.length()==0){
            ResultEngineParts res = new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_chars,kana_chars,new ResultAttr(),this.getClass(),null); // ERROR
            return res;
        }

        ResultEngineParts next_result = null;
        ResultEngineParts now_result = null;
        for(int i=kanji_chars.length();i>0;i--){
            // 次の文字が禁則文字のときにはスキップ
            if(i<kanji_chars.length() && Moji.isKinsoku(kanji_chars.substring(i))){
                continue;
            }

            for(int j=kana_chars.length();j>0;j--){

                // 次の文字が禁則文字のときにはスキップ
                if(j<kana_chars.length() && Moji.isKinsoku(kana_chars.substring(j))){
                    continue;
                }

                String kanji_tmp = kanji_chars.substring(0,i);
                String kana_tmp = kana_chars.substring(0,j);
                // ここでスペースを削除して検査
                now_result = engine.check(kanji_tmp.replace("　",""),kana_tmp.replace("　",""));
                now_result.setEngine(this.getClass());
                if(now_result.isOk()){
                    now_result.setCharParams(kanji_orig,kanji_begin_idx,kanji_begin_idx+i,kana_orig,kana_begin_idx,kana_begin_idx+j);
                    next_result = check_long2short(kanji_orig,kanji_begin_idx+i,kanji_orig.length(),kana_orig,kana_begin_idx+j,kana_orig.length());
                    next_result.setEngine(this.getClass());

                    // OKもしくは最後まで行った場合には終了
                    if(next_result.isOk() ){
                        if(next_result.getKanji().length()==0 && next_result.getKana().length()==0){
                            return now_result;
                        }
                        next_result.setPrevResult(now_result);
                        now_result.setNextResult(next_result);
                        return now_result;
                    }
                    now_result.setNextResult(null); // 削除する
                }
            }
        }
        if(next_result!=null && now_result.getNextResult()==null){
            now_result.setNextResult(next_result);
            next_result.setPrevResult(now_result);
        }else{
            now_result = new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_chars,kana_chars,new ResultAttr(),this.getClass(),null);
            now_result.setCharParams(kanji_orig,kanji_begin_idx,kanji_end_idx,kana_orig,kana_begin_idx,kana_end_idx);
        }
        return now_result;
    }

    @Override
    public ResultEngineParts check(String kanji_part, String kana_part) throws Exception{
        ResultEngineParts result ;
        result = check_long2short(kanji_part,0,kanji_part.length(),kana_part,0,kana_part.length());
        result.setEngine(this.getClass());
        return result;
    }
}
