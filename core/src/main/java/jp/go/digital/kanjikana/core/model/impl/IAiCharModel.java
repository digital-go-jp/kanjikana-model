package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AiCharEngine;


/**
 * AIエンジンを持ちいて、漢字から仮名を推計
 * 文字単位でチェックする。漢字姓名やアルファベット姓名のスペース除去
 * 入力する漢字は、異体字辞書で変換する
 * @since 1.7
 */
public class IAiCharModel extends AiCharModel{
    public IAiCharModel() throws Exception{
        super(new AiCharEngine(true));
    }
}
