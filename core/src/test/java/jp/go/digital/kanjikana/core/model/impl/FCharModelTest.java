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

public class FCharModelTest {

    private ModelIF model;
    {
        try{
            model = new FCharModel();
        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ","タナカ　セルソ　ハナコ"));

        assertThat(modelData.isOk(),equalTo(true));
        assertThat(modelData.getTopResult().isOk(), equalTo(true));

    }

    @Test
    public void test2() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡＨＡＮＡＫＯ","タナカハナコ"));

        assertThat(modelData.isOk(),equalTo(true));
        assertThat(modelData.getTopResult().isOk(), equalTo(true));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＡＮＡＫＡＨＡＮＡＫＯ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("タナカハナコ"));

        assertThat(modelData.getResult(),equalTo("{true;ＴＡＮＡＫＡＨＡＮＡＫＯ;タナカハナコ;FCharEngine;;0:/;0000000}"));

    }

    @Test
    public void test3() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ","スズキ　セルソ　ハナコ"));

        assertThat(modelData.isOk(),equalTo(false));
        assertThat(modelData.getTopResult().isOk(), equalTo(false));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＡＮＡＫＡＣＥＬＳＯＨＡＮＡＫＯ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("スズキセルソハナコ"));

        assertThat(modelData.getResult(),equalTo("{false;ＴＡＮＡＫＡＣＥＬＳＯＨＡＮＡＫＯ;スズキセルソハナコ;FCharEngine;;0:/;0000001}"));

    }


    @Test
    public void test4() throws Exception{
        ModelData modelData = model.run(new ModelData("ＫＯＲＡＬＡ　ＭＵＤＡＬＩＧＥ　ＤＯＮ　ＴＩＬＡＮ　ＳＵＢＵＤＤＩＫＡ","コーララムダリゲ　ドン　ティラーン　スブッディカ"));
        assertThat(modelData.isOk(),equalTo(true));

    }

    @Test
    public void test5() throws Exception{
        ModelData modelData = model.run(new ModelData("ＢＩＬＬＹ　ＳＵＢＵＤＤＩＫＡ","ビリー　スブッディカ"));
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test6() throws Exception{
        ModelData modelData = model.run(new ModelData("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ","タナカ　セルソ　アサコ"));
        assertThat(modelData.isOk(),equalTo(false));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＡＮＡＫＡＣＥＬＳＯＨＡＮＡＫＯ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("タナカセルソアサコ"));

    }
}
