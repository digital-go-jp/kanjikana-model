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

package jp.go.digital.kanjikana.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * マイナカードの券面漢字姓名を，姓名で判定できるように，旧姓，別名などを，それぞれに分割する
 */
public final class KanjiKanaUtil {

    /**
     * 漢字姓名をアルファベット，中国漢字，別名や旧姓に分割する
     *
     * 田中［佐藤］　花子
     * 　　田中　花子
     * 　　佐藤　花子
     *
     * ALEN JHON_李　冬季（鈴木　一郎）
     * 　　ALEN JHON
     * 　　李　冬季
     * 　　鈴木　一郎
     *
     * @param kanji ALEN JHON_李　冬季（鈴木　一郎）
     * @return [ALEN JHON, 李　冬季, 鈴木　一郎]
     */
    public static List<String> kanji_split(String kanji){
        List<String> lst=new ArrayList<>();

        if(kanji.contains("＿")){
            String[] items = kanji.split("＿");
            if(items.length>1) {
                lst.add(items[0]); // アルファベット名

                if (items[1].contains("（")) {
                    String[] itm = items[1].split("（");
                    if (itm.length > 1) {
                        String betsumei = itm[1].replace("）", "");
                        lst.add(itm[0]); // 中国漢字名
                        lst.add(betsumei);
                    } else {
                        lst.add(itm[0]); // 中国漢字名
                    }

                } else {
                    lst.add(items[1]); // 中国漢字名
                }
            }else{
                lst.add(items[0]);
            }
        }else if(kanji.contains("［")) {
            String[] items = kanji.split("［");
            String[] vals = items[1].split("］");

            lst.add(items[0] + vals[1]); // 現状姓と名前
            lst.add(vals[0] + vals[1]); // 旧姓と名前
        }else if(kanji.contains("（")) {
            String[] itm = kanji.split("（");
            if(itm.length>1){
                String betsumei = itm[1].replace("）","");
                lst.add(itm[0]); // 中国漢字名
                lst.add(betsumei);
            }else {
                lst.add(itm[0]); // 中国漢字名
            }
        }else {
            lst.add(kanji);
        }
        return lst;
    }
}
