package jp.go.digital.kanjikana.core.engine.dict.impl;

import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import jp.go.digital.kanjikana.core.utils.Moji;

/**
 * JLIS照会，OSS，クロールで取得した，姓名辞書を保持するシングルトンクラス
 * 最も信頼度が高い辞書
 *  小書き文字を大書文字へ変換と全銀協で使用できない文字を変換する　Moji.normalizeで定義
 */
public class DictReliableNormalized extends DictReliable {

    private DictReliableNormalized() throws Exception {
        super(true);
    }
    /**
     * JLIS辞書を保持するクラスを返す
     * @return 辞書
     * @throws Exception 一般的なエラー
     */
    public synchronized static DictIF newInstance() throws Exception{
        if(dict == null){
            dict = new DictReliableNormalized();
        }
        return dict;
    }

    @Override
    public boolean containsKey(String key) {
        return super.containsKey(Moji.normalize(key));
    }

    @Override
    public boolean containsValueKey(String key, String valueKey) {
        return super.containsValueKey(key, Moji.normalize(valueKey));
    }
}
