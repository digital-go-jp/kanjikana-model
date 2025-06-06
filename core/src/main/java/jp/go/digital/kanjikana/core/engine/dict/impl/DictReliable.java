package jp.go.digital.kanjikana.core.engine.dict.impl;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.engine.dict.Dict;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;

import java.util.Arrays;
import java.util.List;

/**
 * JLIS照会，OSS，クロールで取得した，姓名辞書を保持するシングルトンクラス
 * 最も信頼度が高い辞書
 * オープンソース版では空っぽ
 */
class DictReliable extends Dict {
    protected static DictIF dict = null;
    protected static final String DefaultFile = Resources.getProperty(Resources.PropKey.DIC_RELIABLE);

    protected DictReliable(boolean normalized) throws Exception {
        super(DefaultFile, normalized);
    }

    /**
     * JLIS辞書を保持するクラスを返す
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictReliable(false);
        }
        return dict;
    }
}