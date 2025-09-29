package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AiWordEngine;

/**
 * AIエンジンを持ちいて、漢字から仮名を推計
 * 入力する漢字は、異体字辞書で変換する
 * @since 1.7
 */
public class IAiWordModel extends AiWordModel{
    public IAiWordModel() throws Exception{
        super(new AiWordEngine(true));
    }
}
