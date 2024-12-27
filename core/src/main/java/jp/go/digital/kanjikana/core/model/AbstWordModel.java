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

import jp.go.digital.kanjikana.core.engine.ResultEngineParts;

/**
 * 単語単位で突合するモデルの抽象クラス
 * スペース単位で区切った単語同士で検査する
 */
public abstract class AbstWordModel extends AbstModel{

    /**
     * modelResultを見て，調べる漢字，カナの部分を絞る，
     * WordEngineのものは残す、がCharEngineのものはNGとしてくっつけて再検査
     * @param kanji_item 田中　一郎
     * @param kana_item　タナカ　イチロウ
     * @param modelData　前のモデルの実行結果
     * @return 今回のモデルで計算して追加した実行結果
     */
    @Override
    public ModelData exec(String kanji_item, String kana_item, ModelData modelData)throws Exception{
        modelData.setModel(this.getClass());
        // 初回の時
        if(modelData.getTopResult().getKanji().length()==0){
            String[] kanji_parts = kanji_item.replace("　　","　").split("　");
            String[] kana_parts = kana_item.replace("　　","　").split("　");
            if(kanji_parts.length!=kana_parts.length){
                ResultEngineParts result = new ResultEngineParts(kanji_item,kana_item);
                modelData.setStatus(ModelStatus.NG);
                modelData.setTopResult(result);
                return modelData;
            }
            ResultEngineParts prev=null;
            for(int i=0;i<kanji_parts.length;i++){
                ResultEngineParts now = new ResultEngineParts(kanji_parts[i],kana_parts[i]);
                if(i==0){
                    modelData.setTopResult(now);
                }else{
                    prev.setNextResult(now);
                    now.setPrevResult(prev);
                }
                prev=now;
            }
        }

        ResultEngineParts nowResult = modelData.getTopResult();
        ResultEngineParts newResult = null;
        while(true){
            if(!nowResult.isOk()){

                String[] kanji_lst = nowResult.getKanji().split("　");
                String[] kana_lst = nowResult.getKana().split("　");
                if(kanji_lst.length!=kana_lst.length){
                    kanji_lst=new String[]{nowResult.getKanji()};
                    kana_lst=new String[]{nowResult.getKana()};
                }
                for(int i=0;i<kanji_lst.length;i++){
                    ResultEngineParts res = run_sub(kanji_lst[i],kana_lst[i]);
                    if(newResult==null) {
                        newResult=res;
                    }else{
                        res.setPrevResult(newResult);
                        newResult.setNextResult(res);
                    }
                    newResult = getLastResult(newResult);
                }
            }else{
                if(newResult==null){
                    newResult = new ResultEngineParts(nowResult);
                    newResult.setPrevResult(null);
                    newResult.setNextResult(null);
                }else{
                    newResult.setNextResult(new ResultEngineParts(nowResult));
                    newResult.getNextResult().setNextResult(null);
                    newResult.getNextResult().setPrevResult(newResult); // nowResultを上書きしないようにコピーしたものでセット
                    newResult = getLastResult(newResult);
                }
            }

            if(nowResult.getNextResult()==null){
                break;
            }
            nowResult = nowResult.getNextResult();
        }
        modelData.setTopResult(getTopResult(newResult));
        setStatus(modelData);
        return modelData;
    }
}
