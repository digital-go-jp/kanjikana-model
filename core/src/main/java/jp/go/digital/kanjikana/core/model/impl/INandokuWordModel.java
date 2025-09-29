package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.NandokuEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;

import java.util.Arrays;

/**
 * 法務省の難読名許容の指針
 * 置き字、漢字の読みの一部、熟字訓を許容する
 * 単語単位で実施 「渡辺 雲母」「わたなべ きらら」 渡辺ーわたなべ、雲母ーきらら でチェック
 * 漢字名に異体字があれば置き換える
 * @version 1.8 DictをNormalizedから通常にした。禁則処理の対応のため
 * @since 1.7
 */
public class INandokuWordModel extends NandokuWordModel{

    public INandokuWordModel() throws Exception{
        super(new NandokuEngine(Arrays.asList( DictAsIs.newInstance(),   DictTankanji.newInstance()),true));
    }
}
