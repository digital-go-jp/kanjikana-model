package jp.go.digital.kanjikana.core.engine.ai.classify;

import jp.go.digital.kanjikana.core.Resources;

public class ClassifyEngEngine extends AbstClassifyEngine{
    protected static AbstClassifyEngine model = null;
    protected ClassifyEngEngine() throws Exception {
        super(Resources.getProperty(Resources.PropKey.AI_CLASSIFY_E),Resources.getProperty(Resources.PropKey.AI_CLASSIFY_E_PARAM),Resources.getProperty(Resources.PropKey.AI_CLASSIFY_E_VOCAB));
    }

    /**
     * シングルトンのインスタンスを取得する
     * @return モデルパラメタ
     */
    public static synchronized AbstClassifyEngine newInstance () throws Exception{
        if(model==null){
            model = new ClassifyEngEngine();
        }
        return model;
    }

}
