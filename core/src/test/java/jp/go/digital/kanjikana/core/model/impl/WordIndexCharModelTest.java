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

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class WordIndexCharModelTest {
    private ModelIF model;
    {
        try {
            model = new IDictCharModel();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData modelData = model.run(new ModelData("山本隆行","ヤマモト　タカユキ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));
    }
    @Test
    public void test2() throws Exception{
        ModelData modelData = model.run(new ModelData("ライアン　ディリア恩田","ライアン　ディリア　オンダ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test3() throws Exception{
        ModelData modelData = model.run(new ModelData("田　中　　圭","タナカ　ケイ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }
    @Test
    public void test4() throws Exception{
        ModelData modelData = model.run(new ModelData("大池　翔太","フクロウトショウドウブツノオミセ　タカショウ　ダイヒョウ　オオイケショウタ"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }
    @Test
    public void test5() throws Exception{
        ModelData modelData = model.run(new ModelData("吉田　美紀","ヨシダミキ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test6() throws Exception{
        ModelData modelData = model.run(new ModelData("ベイツ　クリスティーナ早織","ベイツ　クリスティーナ　サオリ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test7() throws Exception{
        ModelData modelData = model.run(new ModelData("山本　＿","ヤマモト タカシ"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test8() throws Exception{
        ModelData modelData = model.run(new ModelData("谷口　＿信","タニグチ タカノブ"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test9() throws Exception{
        ModelData modelData = model.run(new ModelData("我風留　アズィーズウル","アズィズ　ウル　ガフール"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test10() throws Exception{
        ModelData modelData = model.run(new ModelData("横川サムアン　淳子","ヨコカワ　サムアン　アツコ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test11() throws Exception{
        ModelData modelData = model.run(new ModelData("長野　月美","ナガノ　ツキミ　ホジヨニン　イツパンシヤダンホウジン　ソーシヤル　オフイス"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test12() throws Exception{
        ModelData modelData = model.run(new ModelData("リティシェフ　ユーリ誠人","リティシェフ　ユーリ　マコト"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));
    }
    @Test
    public void test12a() throws Exception{
        ModelData modelData = model.run(new ModelData("リティシェフ　ユーリ　誠人","リティシェフ　ユーリ　マコト"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));
    }

    @Test
    public void test13() throws Exception{
        ModelData modelData = model.run(new ModelData("高木　博美ジャネッテ","タカキ　ヒロミ　ジャネッテ"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }


    @Test
    public void test14() throws Exception{
        ModelData modelData = model.run(new ModelData("スール綺愛","スールキア"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));
    }
    @Test
    public void test14a() throws Exception{
        ModelData modelData = model.run(new ModelData("スール　綺愛","スール　キア"));
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));
    }

    @Test
    public void test15() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＫＥＤＡ　ＤＡＳ　ＭＥＲＣＥＳ　ＳＡＮＤＲＡ　ＡＫＥＭＩ","ＴＡＫＥＤＡ　ＤＡＳＭＥＲＣＥＳ"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }

    @Test
    public void test16() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡＬＡ　ＭＵＤＡＬＩＧＥ　ＤＯＮ　ＴＩＬＡＮ　ＳＵＢＵＤＤＩＫＡ","コーララムダリゲ　ドン　ティラーン　スブッディカ"));
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getModel(),equalTo("DictCharModel"));

    }
}

