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

package jp.go.digital.kanjikana.core.executor.generate;

import jp.go.digital.kanjikana.core.engine.ai.SearchResult;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class Kana2KanjiTest {
    private Kana2Kanji kk;
    {
        try {
            kk = new Kana2Kanji();
        }catch(Exception e){
            e.fillInStackTrace();
        }
    }

    @Test
    public void test0() throws Exception{
        Output o = kk.exec("",5);
        assertThat(o.response ,equalTo(Response.E002));
    }

    @Test
    public void test0a() throws Exception{
        Output o = kk.exec("タナカ",0);
        assertThat(o.response ,equalTo(Response.E010));
    }

    @Test
    public void test0b() throws Exception{
        Output o = kk.exec("タナカ",11);
        assertThat(o.response ,equalTo(Response.E010));
    }

    /*
    @Test
    public void test1() throws Exception{
        var res = kk.run("タナカ",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test2() throws Exception{
        var res = kk.run("タロウ",1);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test3() throws Exception{
        var res = kk.run("タナカ　タロウ",5);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

     */
}
