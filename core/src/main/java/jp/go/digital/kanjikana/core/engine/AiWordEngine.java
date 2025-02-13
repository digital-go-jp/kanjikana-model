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

import jp.go.digital.kanjikana.core.engine.ai.AiKanjiKanaModels;
import jp.go.digital.kanjikana.core.engine.ai.BeamSearch;
import jp.go.digital.kanjikana.core.engine.ai.Search;
import jp.go.digital.kanjikana.core.engine.ai.SearchResult;
import jp.go.digital.kanjikana.core.utils.Moji;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * AIを用いて単語単位で突合するエンジン
 */
public class AiWordEngine extends  AbstEngine{
    private static final Logger logger = LogManager.getLogger(AiWordEngine.class);
    public AiWordEngine() throws Exception{
    }

    private boolean checkMoji(String a, String b){
        return Moji.normalize(a).equals(Moji.normalize(b));
    }

    @Override
    public boolean isValidEngine(){
        return true;
    }


    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part 田中　太郎
     * @param kana_part タナカ　タロウ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part, String kana_part){
        Search search =null;
        try {
            search = new BeamSearch(AiKanjiKanaModels.newInstance());
            List<SearchResult> o = search.run(kanji_part);
            int rank=0;
            for(SearchResult r :o){
                if(checkMoji(r.getPredict(),kana_part)){
                    //logger.debug("AiWordEngine.OK;"+r.getProbability());
                    return new ResultEngineParts(ResultEngineParts.Type.OK,kanji_part, kana_part, new ResultAttr("rank:"+rank+";Probability:"+Math.exp(r.getProbability())),this.getClass(),null);
                }
                rank+=1;
            }
        }catch (Exception e){
            e.fillInStackTrace();
            logger.fatal(e);
        }finally{
            if(search!=null){
                search.close();
            }
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(), this.getClass(), null);
    }

}
