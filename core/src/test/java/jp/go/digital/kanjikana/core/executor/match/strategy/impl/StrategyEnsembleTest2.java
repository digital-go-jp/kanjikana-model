package jp.go.digital.kanjikana.core.executor.match.strategy.impl;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyExecutor;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class StrategyEnsembleTest2 {
    // スピードが遅いので確認

    private StrategyIF strategy;
    {
        try{
            strategy = StrategyEnsemble.newInstance();

        }catch(Exception e){
            e.fillInStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        ModelData md = new ModelData("ＮＧＵＹＥＮ　ＶＡＮ　ＤＯＡＮ","グエンヴァン　ドアン");
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
        ModelData md = new ModelData("黒崎　祐理子","マルオ　ユリコ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
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
    }    @Test
    public void test17() throws Exception{
        ModelData md = new ModelData("野中祥子","ノナカショウコ");
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
        assertThat(res, equalTo(false));
    }
}
