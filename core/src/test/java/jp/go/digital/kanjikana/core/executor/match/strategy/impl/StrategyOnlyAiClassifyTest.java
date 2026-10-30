package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyExecutor;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.model.ModelData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * AIの二値分類
 */
public class StrategyOnlyAiClassifyTest {
    private static StrategyIF strategy;
    @BeforeAll
    public static void setUp() throws Exception {
        try{
            strategy = StrategyOnlyAiClassify.newInstance();
        }catch(Exception e){
            e.fillInStackTrace();
        }
    }

    @Test
    public void test0() throws Exception{
        ModelData md = new ModelData("山田　友美","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test0a() throws Exception{
        ModelData md = new ModelData("山田　くるみ","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
    @Test
    public void test0b() throws Exception{
        ModelData md = new ModelData("山田　圭吾","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }





    @Test
    public void test1() throws Exception{
        ModelData md = new ModelData("ＮＧＵＹＥＮ　ＶＡＮ　ＤＯＡＮ","グエンヴァン　ドアン");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test1a() throws Exception{
        ModelData md = new ModelData("ＮＧＵＹＥＮ　ＶＡＮ　ＤＯＡＮ","グエン　ヴァン　ドアン");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test2() throws Exception{
        ModelData md = new ModelData("黒崎　祐理子","マルオ　ユリコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }

    @Test
    public void test3() throws Exception{
        ModelData md = new ModelData("黒崎　祐理子","クロサキ　ユリコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ModelData md = new StrategyExecutor(strategy).run("＿呉　正樹（檜垣　正樹）","ヒガキ　マサキ");
        //boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(md.isOk(), equalTo(true));
    }
    @Test
    public void test5() throws Exception{
        ModelData md = new ModelData("ＡＬＥＪＡＮＤＲＯ　ＪＥＮＡＬＹＮ　ＡＬＩ","アレハンドロ　ジェナリンアリ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test5a() throws Exception{
        ModelData md = new ModelData("ＡＬＥＪＡＮＤＲＯ　ＪＥＮＡＬＹＮ　ＡＬＩ","アレハンドロ　ジェナリン　アリ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test6() throws Exception{
        ModelData md = new ModelData("高尾　千春","エンタ　チハル");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
    @Test
    public void test7() throws Exception{
        ModelData md = new ModelData("中川　夢音","ナカガワ　ロマネ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test8() throws Exception{
        ModelData md = new ModelData("猪股　幹也","イノマタ　ミクヤ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test9() throws Exception{
        ModelData md = new StrategyExecutor(strategy).run("ＫＩＭ　ＹＯＵＮＧ　ＨＩ＿金　英姫（高　英子）","コウ　エイコ");
        //boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(md.isOk(), equalTo(true));
    }
    @Test
    public void test10() throws Exception{
        ModelData md = new ModelData("徐　文旭","ジョ　ブンキョク");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test11() throws Exception{
        ModelData md = new ModelData("ＬＥ　ＱＵＯＣ　ＨＵＹ","レー　クオックフィ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test11a() throws Exception{
        ModelData md = new ModelData("ＬＥ　ＱＵＯＣ　ＨＵＹ","レー　クオック　フィ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test12() throws Exception{
        ModelData md = new StrategyExecutor(strategy).run("ＫＡＪＩＴＡ　ＫＡＩＯ　ＭＡＳＡＭＩＴＳＵ（梶田　正光）","カジタ　マサミツ");
        //boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(md.isOk(), equalTo(true));
    }
    @Test
    public void test13() throws Exception{
        ModelData md = new StrategyExecutor(strategy).run("ＢＵＩ　ＴＨＩ　ＨＡＮＨ","ブイ　テイハイン");
        //boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(md.isOk(), equalTo(true));
    }
    @Test
    public void test14() throws Exception{
        ModelData md = new StrategyExecutor(strategy).run("＿梁　英孝（石井英孝）","イシイ　ヒデタカ");
        //boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(md.isOk(), equalTo(true));
    }
    @Test
    public void test15() throws Exception{
        ModelData md = new ModelData("ＭＹＯ　ＴＨＡＮＤＡＲ　ＯＯ","ミヨー　ダンダーウー");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test16() throws Exception{
        ModelData md = new ModelData("ＳＵＮ　ＹＩ　ＴＩＮＧ","スン イ-テイン");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test16a() throws Exception{
        ModelData md = new ModelData("ＳＵＮ　ＹＩ　ＴＩＮＧ","スン イ- テイン");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test17() throws Exception{
        ModelData md = new ModelData("野中祥子","ノナカショウコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test17a() throws Exception{
        ModelData md = new ModelData("野中　祥子","ノナカ　ショウコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test18() throws Exception{
        ModelData md = new ModelData("ＫＡＮＤＲＡＴＳＥＮ　ＹＡＤＺＶＩＨＡ","カンドラトチェンカヴァ　ヤドヴィガ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test19() throws Exception{
        ModelData md = new ModelData("角掛　文剛","ツノカケ ヨシタカ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test20() throws Exception{
        ModelData md = new ModelData("聶　广義","ジヨウ コウギ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test21() throws Exception{
        ModelData md = new ModelData("山根　教嗣","ヤマネ タカヒデ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
    @Test
    public void test22() throws Exception{
        ModelData md = new ModelData("宮＿　慎二","ミヤザキ シンジ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }


    @Test
    public void test25a() throws Exception{
        ModelData md = new ModelData("ＳＵＭＡＹＹＡＨ　ＡＬＩ　ＡＢＵＬＨＡＤＩＤ　ＡＬＩ","ＳＡＡＤＥＬＤＩＮ　ＡＢＤＵＬＭＯＮＡＭ　ＳＡＹＥＤ　ＡＢＤＵＬＧＡＷＡＤ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
    @Test
    public void test25b() throws Exception{
        ModelData md = new ModelData("ＹＯＵＳＵＦ　ＳＡＡＤＥＬＤＩＮ　ＡＢＤＵＬＭＯＮＡＭ　ＳＡＹＥＤ　ＡＢＤＥＬＧＡＷＡＤ","ＳＡＡＤＥＬＤＩＮ　ＡＢＤＵＬＭＯＮＡＭ　ＳＡＹＥＤ　ＡＢＤＵＬＧＡＷＡＤ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
    @Test
    public void test25c() throws Exception{
        ModelData md = new ModelData("ＹＡＨＩＡ　ＳＡＡＤＥＬＤＩＮ　ＡＢＤＵＬＭＯＮＡＭ　ＳＡＹＥＤ　ＡＢＤＥＬＧＡＷＡＤ","ＳＡＡＤＥＬＤＩＮ　ＡＢＤＵＬＭＯＮＡＭ　ＳＡＹＥＤ　ＡＢＤＵＬＧＡＷＡＤ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }

    @Test
    public void test26() throws Exception{
        ModelData md = new ModelData("ＮＧＵＹＥＮＶＡＮＨＡＩ","グェンヴァンハーイ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }
}
