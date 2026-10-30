package jp.go.digital.kanjikana.core.engine.ai.classify;

import jp.go.digital.kanjikana.core.Resources;

public class ClassifyJpnEngine extends AbstClassifyEngine{
    protected static AbstClassifyEngine model = null;
    protected ClassifyJpnEngine() throws Exception {
        super(Resources.getProperty(Resources.PropKey.AI_CLASSIFY_J),Resources.getProperty(Resources.PropKey.AI_CLASSIFY_J_PARAM), Resources.getProperty(Resources.PropKey.AI_CLASSIFY_J_VOCAB));
    }

    /**
     * シングルトンのインスタンスを取得する
     * @return モデルパラメタ
     */
    public static synchronized AbstClassifyEngine newInstance () throws Exception{
        if(model==null){
            model = new ClassifyJpnEngine();
        }
        return model;
    }
}
