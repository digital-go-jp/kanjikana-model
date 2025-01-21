package jp.go.digital.sample;

import java.util.List;

/**
 * 推論のインターフェース
 */
public interface Search {

    /**
     * Aiモデルを用いて推論結果を返す
     * @param src 入力する文字列，漢字を推論で出したいならばカナ，カナを推論で出したいならば漢字，コンストラクタのAiModelと整合させること
     * @return 推論した結果のリスト
     */
    List<SearchResult> run(String src);

    /**
     * モデルデータを閉じる。Djlライブラリでデータを保持しているので，終了時にこれを呼ばないとメモリリークする
     */
    void close();
}
