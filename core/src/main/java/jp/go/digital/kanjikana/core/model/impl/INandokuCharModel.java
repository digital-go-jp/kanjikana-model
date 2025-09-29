package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.NandokuEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIs;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanji;

import java.util.Arrays;

/**
 * 法務省の難読名許容の指針
 * 置き字、漢字の読みの一部、熟字訓を許容する
 * 文字単位で実施 「渡辺 雲母」「わたなべ きらら」 渡辺雲母ーわたなべきらら でチェック
 * @version 1.8 DictをNormalizedから通常にした。禁則処理の対応のため
 * @since 1.7
 */
public class INandokuCharModel extends NandokuCharModel{
    public INandokuCharModel()throws Exception{
        super(new NandokuEngine(Arrays.asList( DictAsIs.newInstance(),  DictTankanji.newInstance()),true));
    }
}
