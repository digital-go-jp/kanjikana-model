package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelIF;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IAiWordModelTest {

    private ModelIF model ;
    {
        try{
            model = new IAiWordModel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData modelData = model.run(new ModelData("田中　吾郎","タナカ　ゴロウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }

    @Test
    public void test2() throws Exception{
        ModelData modelData = model.run(new ModelData("ＢＩＬＬＹ　吾郎","ビリー　ゴロウ"));
        assertThat(modelData.isOk(),equalTo(true));

    }
}
