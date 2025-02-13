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

import jp.go.digital.kanjikana.core.model.ModelData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FWordEngineTest {

    private EngineIF engine;
    {
        try{
            engine = new FWordEngine();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ResultEngineParts result = engine.check("ＫＯＲＡＬＡ　ＭＵＤＡＬＩＧＥ　ＤＯＮ　ＴＩＬＡＮ　ＳＵＢＵＤＤＩＫＡ","コーララムダリゲ　ドン　ティラーン　スブッディカ");
        assertThat(result.getKana_orig(), equalTo("コーララムダリゲ　ドン　ティラーン　スブッディカ"));
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test1a() throws Exception{
        ResultEngineParts result = engine.check("ＫＯＲＡＬＡＳＵＢＵＤＤＩＫＡ","コーララ　スブッディカ");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test2() throws Exception{
        ResultEngineParts result = engine.check("ＫＯＲＡＬＡＭＵＤＡＬＩＧＥＤＯＮＴＩＬＡＮＳＵＢＵＤＤＩＫＡ","コーララムダリゲドンティラーンスブッディカ");
        assertThat(result.getKana_orig(), equalTo("コーララムダリゲドンティラーンスブッディカ"));
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("ＭＯＨＤ　ＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ","モッド　エーアブ　ビンス　ーロング");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test3a() throws Exception{
        ResultEngineParts result = engine.check("ＭＯＨＤ　ＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ","モッド　エーアブ　ビン　スーロング");
        assertThat(result.isOk(),equalTo(true));
    }
    @Test
    public void test3b() throws Exception{
        ResultEngineParts result = engine.check("ＭＯＨＤ　ＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ","モッド　エーアブ　ビンス　ーロング");
        assertThat(result.isOk(),equalTo(false));
    }


    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("ＭＯＨＤＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ","モッド　エーアブ　ビンス　ーロング");
        assertThat(result.isOk(),equalTo(false));
    }

    @Test
    public void test6() throws Exception{
        ResultEngineParts result = engine.check("ＤＥＮＮＩＳ　ＡＡＢＥＲＧ","デニス　アーバーグ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test7() throws Exception{
        ResultEngineParts result = engine.check("ＬＵ　ＨＳＩＡＯ　ＨＵＩ","シャウフエ");
        assertThat(result.isOk(),equalTo(false));
    }
}
