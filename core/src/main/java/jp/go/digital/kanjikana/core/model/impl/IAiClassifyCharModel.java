package jp.go.digital.kanjikana.core.model.impl;

import jp.go.digital.kanjikana.core.engine.AiClassifyCharEngine;

/**
*/
public class IAiClassifyCharModel extends AiGenerateCharModel {
    public IAiClassifyCharModel() throws Exception{
        super(new AiClassifyCharEngine(true));
    }
}

