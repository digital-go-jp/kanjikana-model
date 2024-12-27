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
 * モデルで判定した結果の，理由を可視化する
 */
public class ResultNoteMaker {

    /**
     * モデルのクラス名の文字列表現
     * @param cls モデルのクラス
     * @return 文字列表現
     */
    public static String basename(Class cls){
        if(cls==null) {
            return "";
        }
        String s = cls.getCanonicalName();
        String[] items = s.split("\\.");
        return items[items.length-1];
    }

    /**
     * 判定した結果の文字列表現を返す
     * @param topResult 漢字カナ突合結果のリストの先頭を入力する
     * @return 文字列表現
     */
    public static String getText(ResultEngineParts topResult){
        StringBuilder sb = new StringBuilder();
        if(topResult==null){
            return "";
        }

        ResultEngineParts nowResult=topResult;
        while(true){
            if(nowResult==null){
                break;
            }
            String engine=basename(nowResult.getEngine());
            String dict = basename(nowResult.getDict());
            String isok = String.valueOf(nowResult.isOk());
            String kanji = nowResult.getKanji();
            String kana = nowResult.getKana();
            String attr=nowResult.getAttr().toText();

            StringBuilder ss = new StringBuilder();
            ss.append("{");

            ss.append(isok);
            ss.append(";");
            ss.append(kanji);
            ss.append(";");
            ss.append(kana);
            ss.append(";");
            ss.append(engine);
            ss.append(";");
            ss.append(dict);
            ss.append(";");
            ss.append(attr);
            ss.append("}");
            sb.append(ss);
            nowResult=nowResult.getNextResult();
        }
        return sb.toString();
    }
}
