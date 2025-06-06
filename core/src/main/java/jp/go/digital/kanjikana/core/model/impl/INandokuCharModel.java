package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.NandokuEngine;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictReliableNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;

import java.util.Arrays;

/**
 * 法務省の難読名許容の指針
 * 置き字、漢字の読みの一部、熟字訓を許容する
 * 文字単位で実施 「渡辺 雲母」「わたなべ きらら」 渡辺雲母ーわたなべきらら でチェック
 * @since 1.7
 */
public class INandokuCharModel extends NandokuCharModel{
    public INandokuCharModel()throws Exception{
        super(new NandokuEngine(Arrays.asList( DictAsIsNormalized.newInstance(),  DictTankanjiNormalized.newInstance()),true));
    }
}
