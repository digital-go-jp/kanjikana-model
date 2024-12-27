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

package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StrategyEnsembleTest {
    private StrategyIF strategy;
    {
        try{
            strategy = StrategyEnsemble.newInstance();
        }catch(Exception e){
            e.fillInStackTrace();
        }
    }

    @Test
    public void test1a() throws Exception{
        ModelData md = new ModelData("山田","サンタ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
        //assertThat(md.getEnsembleResults().size(),equalTo(3));
        String e = md.getResult();
        //System.out.println(e);
        //assertThat(e, equalTo("{false;山田;サンタ;WordEngine;;0:/;}[<{true;山田;サンタ;AiWordEngine;;0:/;rank:3,Probability:0.0014139424012880601}><{true;山;サン;CharEngine;DictTankanji;2:/mj_tankanji/;null}{true;田;タ;CharEngine;DictSeimei;69:/mj_tankanji/nri/;null}><{false;山田;サンタ;WordEngine;;0:/;}>]"));
    }

    @Test
    public void test1() throws Exception{
        ModelData md = new ModelData("山田　花子","サンタ　ハナシ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
        //assertThat(md.getEnsembleResults().size(),equalTo(3));
    }

    @Test
    public void test2() throws Exception{
        ModelData md = new ModelData("山田　花子","ヤマダ　ハナコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
        //assertThat(md.getEnsembleResults().size(),equalTo(3));
    }

    @Test
    public void test3() throws Exception{
        ModelData md = new ModelData("立臼　楓","タチウス　カエデ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
        //assertThat(md.getEnsembleResults().size(),equalTo(3));
    }
}
