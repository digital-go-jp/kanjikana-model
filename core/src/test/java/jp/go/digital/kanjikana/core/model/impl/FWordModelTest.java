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

public class FWordModelTest {
    private ModelIF model;
    {
        try{
            model = new FWordModel();
        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ","タナカ　セルソ　ハナコ"));
        assertThat(modelData.isOk(),equalTo(true));

    }

    @Test
    public void test2() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ","タナカ　セルソ　アサコ"));
        assertThat(modelData.isOk(),equalTo(false));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＡＮＡＫＡ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("タナカ"));

    }

    @Test
    public void test３() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡＬＡ","コーラ"));
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test３a() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＬＡ","コーラ"));
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test３c() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡ","コーラ"));
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test３d() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡＬＡ","コーララ"));
        assertThat(modelData.isOk(),equalTo(true));
    }
}
