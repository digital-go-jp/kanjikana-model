package jp.go.digital.kanjikana.core.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IAiClassifyWordEngineTest {
    private static EngineIF engine;
    @BeforeAll
    public static void setUp() throws Exception {
        try{
            engine = new AiClassifyWordEngine(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("靑陦","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts result = engine.check("青陦","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("靑島","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test6() throws Exception{
        ResultEngineParts result = engine.check("ファンディーニョ　亮真カルロス","リョウマ");
        assertThat(result.isOk(),equalTo(false));
    }
}
