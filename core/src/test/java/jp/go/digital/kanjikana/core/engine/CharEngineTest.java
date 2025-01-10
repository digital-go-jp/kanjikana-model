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

import jp.go.digital.kanjikana.core.engine.dict.impl.DictSeimeiNormalized;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CharEngineTest {

    private EngineIF engine;
    {
        try{
            engine = new CharEngine(false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void test2() throws Exception{
        ResultEngineParts result = engine.check("有芽","ユウメ");
        assertThat(result.isOk(),equalTo(true));
        /*
        assertThat(result.getKanji_orig(),equalTo("有芽"));
        assertThat(result.getNextResult().isOk(),equalTo(false));
        assertThat(result.getNextResult().getKana_orig(),equalTo("ユテ"));
        assertThat(result.getNextResult().getKana_begin_idx(),equalTo(1));
        assertThat(result.getNextResult().getKana_end_idx(),equalTo(2));*/
    }
     @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("有芽","ユウガ");
         assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test3a() throws Exception{
        ResultEngineParts result = engine.check("有　芽","アリメ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts result = engine.check("東京","トウキョウ");
        assertThat(result.isOk(),equalTo(true));
        //assertThat(result.getDict(),equalTo(DictSeimeiNormalized.class));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("東京","トウキヨウ");
        assertThat(result.isOk(),equalTo(true));
        /*
        assertThat(result.getKanji(),equalTo("東京"));
        assertThat(result.getKana(),equalTo("トウキヨウ"));

         */
    }

    @Test
    public void test5a() throws Exception{
        ResultEngineParts result = engine.check("東京","トウ　キヨウ");
        assertThat(result.isOk(),equalTo(true));
        /*
        assertThat(result.getKanji(),equalTo("東京"));
        assertThat(result.getKana(),equalTo("トウキヨウ"));
        assertThat(result.getKana_orig(),equalTo("トウ　キヨウ"));
        */
    }

    @Test
    public void test6() throws Exception{
        ResultEngineParts result = engine.check("山本隆行","ヤマモト　タカユキ");
        assertThat(result.isOk(),equalTo(true));
        /*
        assertThat(result.getKanji(),equalTo("山本"));
        assertThat(result.getKana(),equalTo("ヤマモト"));
        assertThat(result.getKana_orig(),equalTo("ヤマモト　タカユキ"));
        assertThat(result.getNextResult().getNextResult(),equalTo(null));

         */
    }


    @Test
    public void test7() throws Exception{
        ResultEngineParts result = engine.check("梅本　グリレーム　カズオ","ギリレーム　カズオ　ウメモト");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test8() throws Exception{
        ResultEngineParts result = engine.check("花梨","ハナナシ");
        assertThat(result.isOk(),equalTo(true));
        //assertThat(result.getNextResult().isOk(),equalTo(true));
    }

    @Test
    public void test9() throws Exception{
        ResultEngineParts result = engine.check("鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木鈴木","スズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキスズキ");
        assertThat(result.isOk(),equalTo(true));
        //assertThat(result.getNextResult().isOk(),equalTo(true));
    }

    @Test
    public void test14() throws Exception{
        ResultEngineParts result = engine.check("花梨","ハナナシ");
        assertThat(result.isOk(),equalTo(true));
    }
}
