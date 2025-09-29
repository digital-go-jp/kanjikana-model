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

public class Kanji2KanaTest {
    private Kanji2Kana kk;
    {
        try {
            kk = new Kanji2Kana();
        }catch(Exception e){
            e.fillInStackTrace();
        }
    }

    @Test
    public void test0() throws Exception{
        Output o = kk.exec("",5);
        assertThat(o.response ,equalTo(Response.E001));
    }

    @Test
    public void test0a() throws Exception{
        Output o = kk.exec("田中太郎",0);
        assertThat(o.response ,equalTo(Response.E010));
    }

    @Test
    public void test0b() throws Exception{
        Output o = kk.exec("田中太郎",11);
        assertThat(o.response ,equalTo(Response.E010));
    }

    @Test
    public void test1() throws Exception{
        var res = kk.run("田中太郎",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test2() throws Exception{
        var res = kk.run("田中　次郎",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test3() throws Exception{
        var res = kk.run("太郎",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test4() throws Exception{
        var res = kk.run("次郎",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }

    @Test
    public void test5() throws Exception{
        var res = kk.run("田中",10);
        for(SearchResult r:res){
            System.out.println(r);
        }
    }
}
