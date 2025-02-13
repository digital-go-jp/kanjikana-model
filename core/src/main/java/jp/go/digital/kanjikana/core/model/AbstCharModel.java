/*
 * MIT License
 *
 * Copyright (c) 2024 デジタル庁
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.go.digital.kanjikana.core.model;

import jp.go.digital.kanjikana.core.engine.EngineIF;
import jp.go.digital.kanjikana.core.engine.ResultEngineParts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文字モデル
 * 単語単位ではなく，文字単位でずらしながら漢字とカナの一致を判定していく
 * スペースがある場合には無視してくっつける
 * ーー
 * 例
 * 漢字姓名「渡辺一　郎」
 * カナ姓名「ワタナ　ベイチロウ」
 * これをマッチさせるために
 * 「渡」「渡辺」「渡辺ー」「渡辺一郎」と漢字姓名を一文字ずつずらしながら作成
 * 同時にカタカナ姓名を「ワ」「ワタ」「ワタナ」「ワタナベ」「ワタナベイ」「ワタナベイチ」・・　という形で作成しながら＼
 * 辞書とマッチングさせる
 * この場合「渡辺」と「ワタナベ」がマッチするので，その単位でResultEnginePartsを作成していく
 */
public abstract class AbstCharModel extends AbstModel {

    public AbstCharModel(EngineIF engine){
        super(engine);
    }

    /**
     * 単漢字チェックなどでOKだったけど，やっぱりもう一回やり直してみるかどうか。
     * スペース間違いなどの対応のため
     * 現状，全部Falseで返す
     * @param res 単漢字チェック結果
     * @return 判定結果
     */
    protected boolean reDoCheck(ResultEngineParts res){
        return false;
        /* TODO
        if(res.getEngine() == CharEngine.class || res.getEngine()== FCharEngine.class){
            if(res.getDict()== DictAsIs.class || res.getDict() == DictAsIsNormalized.class){
                return false; // AsIsの時は，カタカナ同士やアルファベット同士なので，OKとする
            }
            return true;
        }
        return false;
         */
    }

    /**
     *　漢字カナ突合を実行
     * @param kanji_part　元の単語単位のリスト
     * @param kana_part　元の単語単位のリスト
     * @return 結果
     */
    private ResultEngineParts check(List<String> kanji_part, List<String> kana_part) throws Exception{
        ResultEngineParts res = run_sub(kanji_part.stream().collect(Collectors.joining("　")), kana_part.stream().collect(Collectors.joining("　")));
        return res;
    }

    /**
     * modelResultを見て，調べる漢字，カナの部分を絞る，
     * WordEngineのものは残す、がCharEngineのものはNGとしてくっつけて再検査
     *
     * @param kanji_item
     * @param kana_item
     * @param modelData
     * @return
     */
    @Override
    public ModelData exec(String kanji_item, String kana_item, ModelData modelData) throws Exception{

        if (modelData.getTopResult().getKanji().length() == 0) {
            modelData.setTopResult(new ResultEngineParts(kanji_item, kana_item));
        }

        List<String> kanji_part = new ArrayList<>();// 元の空白区切りの単語単位での単語リスト
        List<String> kana_part = new ArrayList<>(); // 元の空白区切りの単語単位での単語リスト
        ResultEngineParts nowResult = modelData.getTopResult();
        ResultEngineParts newResult = null;// 新しく検査し直したものを入れていく，末尾を保存
        while (true) {

            // NGのもの、もくは、1文字ずつのCharEngineはやり直す
            if (!nowResult.isOk() || reDoCheck(nowResult)) {
                kanji_part.add(nowResult.getKanji());
                kana_part.add(nowResult.getKana());
            } else {
                // 塊ごとに検査する
                if (kana_part.size() > 0) {
                    ResultEngineParts res = check(kanji_part, kana_part);
                    if(newResult==null){
                        newResult=res;
                    }else{
                        newResult.setNextResult(res);
                        res.setPrevResult(newResult);
                    }
                    newResult = getLastResult(newResult);

                }
                if (newResult == null) {
                    newResult = new ResultEngineParts(nowResult);//nowResultに影響がないようにコピー
                    newResult.setPrevResult(null);
                    newResult.setNextResult(null);
                } else {
                    newResult.setNextResult(new ResultEngineParts(nowResult)); //nowResultに影響がないようにコピ
                    newResult.getNextResult().setNextResult(null);
                    newResult.getNextResult().setPrevResult(newResult);
                    newResult = getLastResult(newResult);
                }

                kana_part = new ArrayList<>();
                kanji_part = new ArrayList<>();

            }
            if (nowResult.getNextResult() == null) {
                break;
            }
            nowResult = nowResult.getNextResult();
        }

        // 残りがあれば検査
        if (kana_part.size() > 0) {
            ResultEngineParts res = check(kanji_part, kana_part);
            if(newResult==null){
                newResult=res;
            }else{
                newResult.setNextResult(res);
                res.setPrevResult(newResult);
            }
            newResult = getLastResult(newResult);
        }
        modelData.setTopResult(getTopResult(newResult));
        setStatus(modelData);
        return modelData;
    }
    @Override
    protected ResultEngineParts run_sub(String kanji_str, String kana_str)throws Exception {
        return getEngine().check(kanji_str, kana_str);
    }
}
