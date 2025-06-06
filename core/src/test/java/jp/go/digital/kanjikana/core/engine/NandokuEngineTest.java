package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictReliableNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictUnReliableNormalized;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NandokuEngineTest {

    private NandokuEngine engine;
    {
        try{
            engine = new NandokuEngine(Arrays.asList( DictAsIsNormalized.newInstance(),  DictTankanjiNormalized.newInstance()),true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ResultEngineParts result = engine.check("心愛","ココア");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test2() throws Exception{
        ResultEngineParts result = engine.check("手心㤅","ココア");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("心手愛","ココア");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts result = engine.check("彩夢","ユメ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("樱良","サラ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test5a() throws Exception{
        ResultEngineParts result = engine.check("樱良子","サラ");
        assertThat(result.isOk(),equalTo(true));
    }


    @Test
    public void test6() throws Exception{
        ResultEngineParts result = engine.check("美空","ソラ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test7() throws Exception{
        ResultEngineParts result = engine.check("飛鳥","アスカ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test7a() throws Exception{
        ResultEngineParts result = engine.check("飛鳥矢","アスカ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test8() throws Exception{
        ResultEngineParts result = engine.check("圭吾","トモミ");
        assertThat(result.isOk(),equalTo(false));
    }

}
