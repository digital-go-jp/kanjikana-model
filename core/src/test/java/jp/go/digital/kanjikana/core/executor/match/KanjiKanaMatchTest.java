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

package jp.go.digital.kanjikana.core.executor.match;

import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.Response;
import jp.go.digital.kanjikana.core.executor.StatusMatch;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyBasic;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyEnsemble;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class KanjiKanaMatchTest {

    private KanjiKanaMatch simple;
    private KanjiKanaMatch detail;
    {
        try {
            simple = new KanjiKanaMatch(StrategyBasic.newInstance());
            detail = new KanjiKanaMatch(StrategyEnsemble.newInstance());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test0() throws Exception{
        Output o = simple.exec("","");
        assertThat(o.response ,equalTo(Response.E001));
    }

    @Test
    public void test0a() throws Exception{
        Output o = simple.exec("山田　太郎","");
        assertThat(o.response ,equalTo(Response.E002));
    }

    @Test
    public void test0b() throws Exception{
        Output o = simple.exec("","ヤマダ　タロウ");
        assertThat(o.response ,equalTo(Response.E001));
    }

    @Test
    public void test2() throws Exception{
        Output o = simple.exec("山田　太郎","ヤマダ　タロウ");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.OK));
    }

    @Test
    public void test2a() throws Exception{
        Output o = detail.exec("山田　太郎","ヤマダ　タロウ");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.OK));
    }

    @Test
    public void test3() throws Exception{
        Output o = detail.exec("山田　太郎","サンダ　フトロ");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.ENSEMBLE1));
    }


    @Test
    public void test4() throws Exception{
        Output o = detail.exec("山田","サンデン");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.ENSEMBLE3));

    }

    @Test
    public void test4a() throws Exception{
        Output o = simple.exec("山田","サンデン");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.NG));

    }


    @Test
    public void test5() throws Exception{
        Output o = simple.exec("ＨＥ　ＪＵＮ＿何　俊","カ　シユン");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.OK));
    }

    @Test
    public void test5a() throws Exception{
        Output o = detail.exec("ＨＥ　ＪＵＮ＿何　俊","カ　シユン");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.OK));
    }

    @Test
    public void test5b() throws Exception{
        Output o = detail.exec("ＨＥ　ＪＵＮ","カ　シユン");
        assertThat(o.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_STATUS),equalTo(StatusMatch.NG));
    }
}
