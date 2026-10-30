package jp.go.digital.kanjikana.core.engine;


/**
 * AIの２値分類を用いて文字単位で突合するエンジン
 */
public class AiClassifyCharEngine extends AiClassifyWordEngine {

    public AiClassifyCharEngine(boolean hasItaiji) throws Exception{
        super(hasItaiji);
    }

    /**
     * 単語単位，文字単位を入力としてマッチング
     * @param kanji_part 田中　太郎
     * @param kana_part タナ　カタロウ
     * @return 直近のEnginePartsResult
     */
    @Override
    public ResultEngineParts check(String kanji_part, String kana_part){
        ResultEngineParts result = super.check(kanji_part.replace("　",""),kana_part.replace("　",""));
        result.setEngine(this);
        result.setCharParams(kanji_part,0,kanji_part.length(),kana_part,0,kana_part.length());
        return result;
    }
}
