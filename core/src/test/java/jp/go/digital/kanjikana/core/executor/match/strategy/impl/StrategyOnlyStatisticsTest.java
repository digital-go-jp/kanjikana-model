package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.model.ModelData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;

public class StrategyOnlyStatisticsTest {

    private StrategyIF strategy;
    {
        try{
            strategy = StrategyOnlyStatistics.newInstance();

        }catch(Exception e){
            e.fillInStackTrace();
        }
    }


    @Test
    public void test1() throws Exception{
        ModelData md = new ModelData("山田　友美","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(true));
    }

    @Test
    public void test1a() throws Exception{
        ModelData md = new ModelData("山田　くるみ","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
    @Test
    public void test1b() throws Exception{
        ModelData md = new ModelData("山田　圭吾","ヤマダ　トモミ");
        boolean res = strategy.modelCheck(md, md.getKanji(), md.getKana());
        assertThat(res, equalTo(false));
    }
}
