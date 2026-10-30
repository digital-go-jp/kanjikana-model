package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AiClassifyWordModelTest {
    private static ModelIF model;
    @BeforeAll
    public static void setUp() throws Exception {
        try{
            model = new AiClassifyWordModel();
        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData modelData = model.run(new ModelData("ＵＭＥＭＯＴＯ　ＧＵＩＬＨＥＲＭＥ　ＫＡＺＵＯ",",ウメモトギリレーム　カズオ"));
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test1a() throws Exception{
        ModelData modelData = model.run(new ModelData("ＵＭＥＭＯＴＯ　ＧＵＩＬＨＥＲＭＥ　ＫＡＺＵＯ",",ウメモト　ギリレーム　カズオ"));
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test1b() throws Exception{
        ModelData modelData = model.run(new ModelData("梅本　カズオ","ウメモトカズオ"));
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test1c() throws Exception{
        ModelData modelData = model.run(new ModelData("梅本　カズオ","ウメモト　カズオ"));
        assertThat(modelData.isOk(),equalTo(true));
    }



    @Test
    public void test2() throws Exception{
        ModelData modelData = model.run(new ModelData("靑陦　カズオ","アオシマ　カズオ"));
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test2a() throws Exception{
        ModelData modelData = model.run(new ModelData("靑島　カズオ","アオシマ　カズオ"));
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test2b() throws Exception{
        ModelData modelData = model.run(new ModelData("青陦　カズオ","アオシマ　カズオ"));
        assertThat(modelData.isOk(),equalTo(true));
    }
}
