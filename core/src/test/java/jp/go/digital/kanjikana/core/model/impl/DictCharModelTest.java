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

import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.model.ModelData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DictCharModelTest {
    private DictCharModel model ;
    {
        try{
            model = new DictCharModel();
        }catch(Exception e){
            e.fillInStackTrace();
        }
    }
    @Test
    public void test1() throws Exception{
        ModelData md = model.run(new ModelData("山田　太郎","ヤマダタロウ"));
        assertThat(md.isOk(),equalTo(true));
    }

    @Test
    public void test2() throws Exception{
        ModelData md = model.run(new ModelData("山田　惇太","ヤマダ　シュンタ"));
        assertThat(md.isOk(),equalTo(true));
    }





    @Test
    public void test3() throws Exception{
        ModelData modelData = model.run(new ModelData("田中　芽有","タナカメユウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }
    @Test
    public void test4() throws Exception{
        ModelData modelData = model.run(new ModelData("田中芽有　東京","タナカメユ　トウキョウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }


    /**
     * 漢字カナ同じもの
     * @throws Exception
     */
    @Test
    public void test5() throws Exception{
        ModelData modelData = model.run(new ModelData("たなか　とうきょう","タナカ　トウキョウ"));
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test6() throws Exception{
        ModelData modelData = model.run(new ModelData("山本隆行","ヤマモト　タカユキ"));
        assertThat(modelData.isOk(),equalTo(true));
    }


    @Test
    public void test7() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡＬＡ　ＭＵＤＡＬＩＧＥ　ＤＯＮ　ＴＩＬＡＮ　ＳＵＢＵＤＤＩＫＡ","コーララ　ムダリゲ　ドン　ティラーン　スブッディカ"));
        assertThat(modelData.isOk(),equalTo(false));

    }


    @Test
    public void test8() throws Exception{
        ModelData modelData = model.run(new ModelData("田中　吾郎","タナカ　ゴロウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }


    @Test
    public void test9() throws Exception{
        ModelData modelData = model.run(new ModelData("田中　有芽田中　芽有","タナカ　ユメタナカ　ガユウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }
}
