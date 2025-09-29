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

package jp.go.digital.kanjikana.core.executor.match.strategy;

import jp.go.digital.kanjikana.core.engine.FWordEngine;
import jp.go.digital.kanjikana.core.engine.WordEngine;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyBasic;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StrategyExecutorBasicTest {
    StrategyExecutor executor;
    {
        try {
            executor = new StrategyExecutor(StrategyBasic.newInstance());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void test01() throws Exception{
        ModelData modelData = executor.run("0001,田中　有芽田中　芽有,タナカ　ユカタナカ　カユ",1,2,",");
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test01a() throws Exception{
        ModelData modelData = executor.run("0001,田中　有芽田中　芽有,タナカ　ユメタナカ　メユ",1,2,",");
        assertThat(modelData.isOk(),equalTo(true));
    }


    @Test
    public void test02() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＪＯＥＬ,ビリージョエル",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
        //assertThat(modelData.getResult(),equalTo("{true;ＢＩＬＬＹ;ビリー;CharEngine;DictSeimei;54:/di/nri/;null}{true;ＪＯＥＬ;ジョエル;CharEngine;DictSeimeiNormalized;18:/nri/;null}"));
    }

    @Test
    public void test03() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＪＯＥＬ,ビリー　ジョエル",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test04() throws Exception{
        ModelData modelData = executor.run("ＤＥＮＮＩＳ　ＡＡＢＥＲＧ,デニス　アーバーグ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test05() throws Exception{
        ModelData modelData = executor.run("ＴＯＲＳＴＥＩＮ　ＡＡＧＡＡＲＤ　ＮＩＬＳＥＮ,トリスタン　アーガールド　ニールセン",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        assertThat(modelData.getTopResult().getEngine(),equalTo(FWordEngine.class));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＯＲＳＴＥＩＮ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("トリスタン"));
        assertThat(modelData.getTopResult().getNextResult().getEngine(),equalTo(FWordEngine.class));
        assertThat(modelData.getTopResult().getNextResult().getKanji(),equalTo("ＡＡＧＡＡＲＤ"));
        assertThat(modelData.getTopResult().getNextResult().getKana(),equalTo("アーガールド"));
    }
    @Test
    public void test05a() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＡＡＧＡＡＲＤ　ＮＩＬＳＥＮ,ビリー　アーガールド　ニールセン",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        /*
        assertThat(modelData.getTopResult().getEngine(),equalTo(WordEngine.class));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＢＩＬＬＹ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("ビリー"));
        assertThat(modelData.getTopResult().getNextResult().getEngine(),equalTo(FWordEngine.class));
        assertThat(modelData.getTopResult().getNextResult().getKanji(),equalTo("ＡＡＧＡＡＲＤ"));
        assertThat(modelData.getTopResult().getNextResult().getKana(),equalTo("アーガールド"));
        */

    }

    @Test
    public void test06() throws Exception{
        ModelData modelData = executor.run("ＴＯＲＳＴＥＩＮ　ＡＡＧＡＡＲＤ　ＮＩＬＳＥＮ,トリスタン　アーガールド　ニールセン",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }


    @Test
    public void test07() throws Exception{
        ModelData modelData = executor.run("日本［東京］　花子,ニホン　ハナコ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getTopResult().getKanji(),equalTo("日"));
        //assertThat(modelData.getTopResult().getKana(),equalTo("ニ"));
        //assertThat(modelData.getTopResult().getDict(),equalTo(DictSeimei.class));
    }

    @Test
    public void test07a() throws Exception{
        ModelData modelData = executor.run("日本［東京］　花子,ニッポン　ハナコ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getTopResult().getKanji(),equalTo("日"));
        //assertThat(modelData.getTopResult().getKana(),equalTo("ニ"));
        //assertThat(modelData.getTopResult().getDict(),equalTo(DictSeimei.class));
    }

    @Test
    public void test08() throws Exception{
        ModelData modelData = executor.run("日本［東京］　花子,トウキョウ　ハナコ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        assertThat(modelData.getTopResult().getKanji(),equalTo("東京"));
        assertThat(modelData.getTopResult().getKana(),equalTo("トウキョウ"));
        //assertThat(modelData.getTopResult().getDict(),equalTo(DictSeimeiNormalized.class));
    }

    @Test
    public void test09() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＪＯＥＬ＿山田　太郎（田中　太郎）,ヤマダ　タロウ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test10() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＪＯＥＬ＿山田　太郎（田中　芽有）,タナカ　ガユ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        //assertThat(modelData.getTopResult().getKanji(),equalTo("田中"));
        //assertThat(modelData.getTopResult().getNextResult().getKanji(),equalTo("芽"));
        //assertThat(modelData.getTopResult().getNextResult().getNextResult().getKana(),equalTo("ユ"));
    }
    @Test
    public void test11() throws Exception{
        ModelData modelData = executor.run("ＢＩＬＬＹ　ＪＯＥＬ＿山田　太郎（田中　芽有）,ビリー　ジョエル",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test12() throws Exception{
        ModelData modelData = executor.run("＿山田　太郎（田中　芽有）,ビリー　ジョエル",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test13() throws Exception{
        ModelData modelData = executor.run("＿山田　太郎（田中　芽有）,タナカ　メユ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test14() throws Exception{
        ModelData modelData = executor.run("山田　＿郎,ヤマダ　タロウ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test15() throws Exception{
        ModelData modelData = executor.run("山田　＿,ヤマダ　ハジメ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test16() throws Exception{
        ModelData modelData = executor.run("やまだ　一,ヤマダ　ハジメ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test17() throws Exception{
        ModelData modelData = executor.run("ＴＡＫＥＤＡ　ＤＡＳ　ＭＥＲＣＥＳ　ＳＡＮＤＲＡ　ＡＫＥＭＩ（メルセス　サンドラ　アケミ　タケダ　ダス）,ＴＡＫＥＤＡ　ＤＡＳＭＥＲＣＥＳ",0,1,",");

        assertThat(modelData.isOk(),equalTo(false));
    }



    @Test
    public void test18() throws Exception{

        ModelData modelData = executor.run("ＫＯＲＡＬＡ　ＭＵＤＡＬＩＧＥ　ＤＯＮ　ＴＩＬＡＮ　ＳＵＢＵＤＤＩＫＡ,コーララムダリゲ　ドン　ティラーン　スブッディカ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test19() throws Exception{

        ModelData modelData = executor.run("ＢＩＬＬＹ　ＫＯＲＡ　ＢＩＬＬＹ　ＫＯＬＡ　ＳＵＢＵＤＤＩＫＡ,ビリー　コーラ　ビリー　コーラ　スブッディカ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test19a() throws Exception{

        ModelData modelData = executor.run("ＢＩＬＬＹ　ＫＯＲＡＬＡ　ＢＩＬＬＹ　ＫＯＲＡＬＡ　ＳＵＢＵＤＤＩＫＡ　ＫＯＲＡＬＡ　,ビリー　コーララ　ビリー　コーララ　スブッディカ　コーララ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test19b() throws Exception{

        ModelData modelData = executor.run("ＫＯＲＡＬＡ　ＫＯＲＡＬＡ　ＳＵＢＵＤＤＩＫＡ　ＫＯＲＡＬＡ　,コーララ　コーララ　スブッディカ　コーララ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test19c() throws Exception{

        ModelData modelData = executor.run("ＫＯＲＡＬＡ,コーララ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));

    }

    @Test
    public void test20() throws Exception{
        ModelData modelData = executor.run("ジャウ　ファティマタビンタラスール綺愛,ジャウ　ファティマタビンタラスールリア",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test20a() throws Exception{
        ModelData modelData = executor.run("ジャウ　ファティマタビンタラスール　綺愛,ジャウ　ファティマタビンタラスール　リア",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test20b() throws Exception{
        ModelData modelData = executor.run("ジャウ　ファティマタビンタラスール　綺愛,ジャウ　ファティマタビンタラスール　キア",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }
    @Test
    public void test20c() throws Exception{
        ModelData modelData = executor.run("ジャウ　ファティマタビンタラスール綺愛,ジャウ　ファティマタビンタラスールキア",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test21() throws Exception{

        ModelData modelData = executor.run("ＴＡＮＡＫＡ　ＣＥＬＳＯ　ＨＡＮＡＫＯ,タナカ　セルソ　ハナコ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
        assertThat(modelData.getTopResult().getKanji(),equalTo("ＴＡＮＡＫＡ"));
        assertThat(modelData.getTopResult().getKana(),equalTo("タナカ"));

        assertThat(modelData.getTopResult().getNextResult().getPrevResult().getKanji(),equalTo("ＴＡＮＡＫＡ"));
        assertThat(modelData.getTopResult().getNextResult().getPrevResult().getKana(),equalTo("タナカ"));
        assertThat(modelData.getTopResult().getNextResult().getKanji(),equalTo("ＣＥＬＳＯ"));
        assertThat(modelData.getTopResult().getNextResult().getKana(),equalTo("セルソ"));
        assertThat(modelData.getTopResult().getNextResult().getNextResult().getPrevResult().getKanji(),equalTo("ＣＥＬＳＯ"));
        assertThat(modelData.getTopResult().getNextResult().getNextResult().getPrevResult().getKana(),equalTo("セルソ"));
        assertThat(modelData.getTopResult().getNextResult().getNextResult().getKanji(),equalTo("ＨＡＮＡＫＯ"));
        assertThat(modelData.getTopResult().getNextResult().getNextResult().getKana(),equalTo("ハナコ"));
        //assertThat(modelData.getResult(),equalTo("{true;ＴＡＮＡＫＡ;タナカ;WordEngine;DictSeimei;1554:/digitalD/digitalN/;null}{true;ＣＥＬＳＯ;セルソ;WordEngine;DictSeimei;94:/digitalN/;null}{true;ＨＡＮＡＫＯ;ハナコ;WordEngine;DictSeimei;30:/digitalD/digitalN/;null}"));

    }


    @Test
    public void test22() throws Exception{
        ModelData modelData = executor.run("＿宋　弘明（田中　弘明）,タカナ　ヒロアキ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test23() throws Exception{
        ModelData modelData = executor.run("レフトサーリ　サミペッカ神之助,レフトサーリ　サミペッカ　シンノスケ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test24() throws Exception{
        ModelData modelData = executor.run("ＵＭＥＭＯＴＯ　ＧＵＩＬＨＥＲＭＥ　ＫＡＺＵＯ（梅本　グリレーム　カズオ）,ギリレーム　カズオ　ウメモト",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test25() throws Exception{
        ModelData modelData = executor.run("川崎　メアリーケイト,カワサキ　メアリー　ケイト",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test26() throws Exception{
        ModelData modelData = executor.run("フリードライン　カーター輝竜,フリードライン　カーター　キリュウ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test27() throws Exception{
        ModelData modelData = executor.run("ＭＡＲＩＡ　ＫＩＴＡＭＵＲＡ　ＰＡＭＥＬＡ,キタムラパメラマリア",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }

    @Test
    public void test28() throws Exception{
        ModelData modelData = executor.run("ＣＨＥＮ　ＣＨＩＥＮ　ＣＨＵＮＧ＿陳　建中,チン　ケンチュウ",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test29() throws Exception{
        ModelData modelData = executor.run("カルデロンファンディーニョ　亮真カルロス,カルデロン　リョウマ",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }


    @Test
    public void test30() throws Exception{
        ModelData modelData = executor.run("ＭＯＨＤ　ＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ,モッド　エーアブ　ビンス　ーロング",0,1,",");
        assertThat(modelData.isOk(),equalTo(false));
    }
    @Test
    public void test30a() throws Exception{
        ModelData modelData = executor.run("ＭＯＨＤ　ＡＹＵＢ　ＢＩＮ　ＳＵＬＯＮＧ,モッド　エーアブ　ビン　スーロング",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

    @Test
    public void test40() throws Exception{
        ModelData modelData = executor.run("ＨＥ　ＪＵＮ＿何　俊,カ　シユン",0,1,",");
        assertThat(modelData.isOk(),equalTo(true));
    }

}
