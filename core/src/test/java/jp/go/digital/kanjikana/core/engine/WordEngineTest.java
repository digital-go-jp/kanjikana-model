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
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WordEngineTest {

    private WordEngine engine;
    {
        try{
            engine = new WordEngine(false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ResultEngineParts result = engine.check("田中","タナカ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test2() throws Exception{
        ResultEngineParts result = engine.check("田中","ワタナベ");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("靑陦","アオシマ");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts result = engine.check("青陦","アオシマ");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("靑島","アオシマ");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test6() throws Exception{
        ResultEngineParts result = engine.check("東京","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test7() throws Exception{
        ResultEngineParts result = engine.check("トウキョウ","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
        assertThat(result.getDict(),equalTo(DictAsIs.class));
    }

    @Test
    public void test8() throws Exception{
        ResultEngineParts result = engine.check("トウキヨウ","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
        assertThat(result.getDict(),equalTo(DictAsIsNormalized.class));
    }

    @Test
    public void test9() throws Exception{
        ResultEngineParts result = engine.check("とうきょう","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
        assertThat(result.getDict(),equalTo(DictAsIsNormalized.class));
    }

    @Test
    public void test10() throws Exception{
        ResultEngineParts result = engine.check("とうきよう","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
        assertThat(result.getDict(),equalTo(DictAsIsNormalized.class));
    }

    @Test
    public void test11() throws Exception{
        ResultEngineParts result = engine.check("ＢＩＬＬＹ","ＢＩＬＬＹ");
        assertThat(result.isOk(),equalTo(true));
        assertThat(result.getDict(),equalTo(DictAsIs.class));
    }

    @Test
    public void test12() throws Exception{
        ResultEngineParts result = engine.check("ル綺愛","ルリア");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test13() throws Exception{
        ResultEngineParts result = engine.check("吾郎","ゴロウ");
        assertThat(result.isOk(),equalTo(true));
    }
    @Test
    public void test14() throws Exception{
        ResultEngineParts result = engine.check("花梨","ハナナシ");
        assertThat(result.isOk(),equalTo(true));
    }
    @Test
    public void test15() throws Exception{
        ResultEngineParts result = engine.check("梅本","ギ");
        assertThat(result.isOk(),equalTo(false));
    }

}
