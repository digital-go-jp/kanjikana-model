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

package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.model.AbstWordModel;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import jp.go.digital.kanjikana.core.engine.AiWordEngine;
import jp.go.digital.kanjikana.core.engine.ResultAttr;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AIモデル
 * スペースで区切られた単語単位で突合
 */
public final class AiWordModel extends AbstWordModel{
    private static final Logger logger = LogManager.getLogger(AiWordModel.class);

    public AiWordModel() throws Exception{
        super(new AiWordEngine());
    }

    @Override
    public ModelData run(ModelData modelData) throws Exception {
        String kanji_item = modelData.getKanji();
        String kana_item = modelData.getKana();
        return run(kanji_item, kana_item, modelData);
    }

    @Override
    public ModelData run(String kanji_item, String kana_item, ModelData modelData) throws Exception {
        modelData.setModel(this.getClass());
        //modelData.resetTopResult();
        ModelData md = exec(kanji_item, kana_item, modelData);
        md.setModel(this.getClass());
        return md;
    }

    @Override
    protected ResultEngineParts run_sub(String kanji_str, String kana_str) throws Exception{
        ResultEngineParts topResult = null; // WordEngine
        String[] kanji_parts = kanji_str.split("　");
        String[] kana_parts = kana_str.split("　");
        if(kanji_parts.length==kana_parts.length) {
            ResultEngineParts prevResult = null;
            ResultEngineParts nowResult = null;
            for (int i = 0; i < kanji_parts.length; i++) {
                nowResult = getEngine().check(kanji_parts[i], kana_parts[i]);

                logger.debug("AiWordModel.i="+i+",nowResult;"+nowResult.isOk());
                if (prevResult != null) {
                    prevResult.setNextResult(nowResult);
                    nowResult.setPrevResult(prevResult);
                }
                prevResult = nowResult;
                if (topResult == null) {
                    topResult = prevResult;
                }
            }
        }
        if(topResult!=null){
            logger.debug("AiWordModel.OK");
            return topResult;
        }else{
            logger.debug("AiWordModel.NG");
            return new ResultEngineParts(ResultEngineParts.Type.NOT_FOUND,kanji_str,kana_str,new ResultAttr(), this.getClass(),null);
        }

    }
}