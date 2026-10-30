package jp.go.digital.kanjikana.core.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AiClassifyWordEngineTest {
    private static EngineIF engine;
    @BeforeAll
    public static void setUp() throws Exception {
        try {
            engine = new AiClassifyWordEngine(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ResultEngineParts r = engine.check("太郎","タロウ");
        assertThat(r.isOk(),equalTo(true));
    }

    @Test
    public void test2() throws Exception{
        ResultEngineParts r = engine.check("","タロウ");
        System.out.println(r);
        assertThat(r.isOk(),equalTo(false));
    }


    @Test
    public void test3a() throws Exception{
        ResultEngineParts r = engine.check("田中太郎","タナカタロウ");
        assertThat(r.isOk(),equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts r = engine.check("ＢＩＬＬＹ","ビリー");
        assertThat(r.isOk(),equalTo(true));
    }

    @Test
    public void test4a() throws Exception{
        ResultEngineParts r = engine.check("ＪＯＥＬ","ジョエル");
        assertThat(r.isOk(),equalTo(true));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts r = engine.check("ＭＩＣＨＥＬ","ミッシェル");
        assertThat(r.isOk(),equalTo(true));
    }

    @Test
    public void test5a() throws Exception{
        ResultEngineParts r = engine.check("ＪＡＣＫＳＯＮ","ジャクソン");
        assertThat(r.isOk(),equalTo(true));
    }
}
