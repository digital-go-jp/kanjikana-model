package jp.go.digital.kanjikana.core.engine.ai.classify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class AiClassifyTest {
    private AiClassify classify_j;
    private AiClassify classify_e;

    @BeforeEach
    public void setUp() throws Exception {
        classify_j = new AiClassify(ClassifyJpnEngine.newInstance());
        classify_e = new AiClassify(ClassifyEngEngine.newInstance());
    }

    @Test
    public void test1() throws Exception {
        var res = classify_j.run( "漢字","カンジ");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }

    @Test
    public void test2() throws Exception {
        var res = classify_j.run( "山田","ヤマダ");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }

    @Test
    public void test2a() throws Exception {
        var res = classify_e.run( "ＢＩＬＬＹ","ビリー");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }

    @Test
    public void test2b() throws Exception {
        var res = classify_e.run( "ＭＩＣＨＥＬ","ミッシェル");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }

    @Test
    public void test2c() throws Exception {
        var res = classify_e.run( "ＭＩＣＨＥＬ","マイケル");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }

    @Test
    public void test3() throws Exception {
        var res = classify_j.run( "田中","ヤマダ");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(false));
    }

    @Test
    public void test4() throws Exception {
        var res = classify_j.run( "有芽","ユウガ");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(false));
    }

    @Test
    public void test5() throws Exception {
        var res = classify_j.run( "晴子","ハルコ");
        System.out.println(res);
        assertThat(res.isOk(),equalTo(true));
    }
}
