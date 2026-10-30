package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AiClassifyWordEngine;

public class IAiClassifyWordModel extends AiGenerateWordModel {
    public IAiClassifyWordModel() throws Exception{
        super(new AiClassifyWordEngine(true));
    }
}
