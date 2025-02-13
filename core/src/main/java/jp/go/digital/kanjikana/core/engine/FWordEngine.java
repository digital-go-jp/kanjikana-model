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

import jp.go.digital.kanjikana.core.engine.foreigner.Foreigner;
import jp.go.digital.kanjikana.core.engine.foreigner.ForeignerOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 外国人モデルを用いて，単語単位にチェックする
 */
public class FWordEngine extends  AbstEngine{
    private static final Logger logger = LogManager.getLogger(FWordEngine.class);

    public FWordEngine() throws Exception{
    }

    @Override
    public boolean isValidEngine(){
        return true;
    }


    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part ＴＡＮＡＫＡ
     * @param kana_part タナカ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part, String kana_part){
        try {
            Foreigner foreigner = new Foreigner();
            ForeignerOutput o = foreigner.run(kanji_part, kana_part);
            if(o.getCode().endsWith("0")){
                return new ResultEngineParts(ResultEngineParts.Type.OK,kanji_part,kana_part,new ResultAttr(o.getCode()), this.getClass(), null);
            }else{
                return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(o.getCode()), this.getClass(), null);

            }
        }catch (Exception e){
            e.fillInStackTrace();
            logger.fatal(e);
        }
        return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_part,kana_part,new ResultAttr(), this.getClass(), null);
    }
}
