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

package jp.go.digital.kanjikana.core.executor.match.strategy;

import jp.go.digital.kanjikana.core.model.ModelStatus;
import jp.go.digital.kanjikana.core.utils.Moji;
import jp.go.digital.kanjikana.core.utils.KanjiKanaUtil;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * impl以下で定義されたストラテジーを実行するクラス
 */
public class StrategyExecutor {
    private static final Logger logger = LogManager.getLogger(StrategyExecutor.class);

    private final StrategyIF strategy;

    /**
     * コンストラクタ
     * @param strategy　Impl以下のストラテジ
     */
    public StrategyExecutor(StrategyIF strategy){
        this.strategy = strategy;
    }

    /**
     * 入力をもとに漢字とカナが一致しているかどうか判定
     * @param kanji_str 漢字姓名， ＢＩＬＬＹ　ＪＯＥＬ＿山田　太郎（田中　太郎） 内部でMoji.normalize_basic　で変換する
     * @param kana_str　カナ姓名　ヤマダ　タロウ 内部でoji.normalize_basic　で変換する
     * @return 判定結果データ
     * @throws Exception 一般的なエラー
     */
    public ModelData run(String kanji_str, String kana_str) throws Exception{
        logger.debug("kanji_str="+kanji_str+",kana_str="+kana_str);

        ModelData modelData = new ModelData(kanji_str, kana_str);

        String kanjis = Moji.normalize_basic(kanji_str);// 日本［東京］　花子
        String kana  = Moji.normalize_basic(kana_str);// カナの姓名が入っている　　　ニホン　ハナコ
        List<String> kanji_items = KanjiKanaUtil.kanji_split(kanjis); // 複数名前があるときはそれぞれ入っている。旧姓名と現姓名などが２つ入るイメージ　['日本　花子','東京　花子']
        for(String kanji: kanji_items) {
            if(kanji.length()==0){
                continue;
            }
            // 前の漢字（日本　花子）でのアンサンブルの結果は削除する
            modelData.getEnsembleResults().clear();
            if(strategy.modelCheck(modelData,kanji,kana)){
                break;
            }
        }
        return modelData;
    }

    /**
     * 入力をもとに漢字とカナが一致しているかどうか判定
     * @param line　漢字とカナ，その他の情報が行で入っている
     * @param kanji_idx　漢字姓名がline内で入っている位置，０オリジン
     * @param kana_idx　カナ姓名がline内で入っている位置，０オリジン
     * @param separator　lineのセパレータ，
     * @return 結果データ
     * @throws Exception 一般的なエラー
     */
    public ModelData run(String line, int kanji_idx, int kana_idx, String separator) throws Exception{
        logger.debug("line="+line);

        String[] items = line.split(separator);
        if(kanji_idx>=items.length || kanji_idx < 0){
            return new ModelData(line, null, ModelStatus.E001);
        }
        if(kana_idx>=items.length || kana_idx < 0){
            return new ModelData(line , null, ModelStatus.E002);
        }

        String kanji = items[kanji_idx];
        String kana = items[kana_idx];
        return run(kanji, kana);
    }

    /**
     * 入力をもとに漢字とカナが一致しているかどうか判定，出力をWeb用に
     * @param kanji_str 漢字姓名， ＢＩＬＬＹ　ＪＯＥＬ＿山田　太郎（田中　太郎） 内部でMoji.normalize_basic　で変換する
     * @param kana_str　カナ姓名　ヤマダ　タロウ 内部でoji.normalize_basic　で変換する
     * @return 判定結果データ
     * @throws Exception 一般的なエラー
     */
    public Output exec(String kanji_str, String kana_str) throws Exception{
        ModelData md = run(kanji_str, kana_str);
        return OutputMaker.exec(md);
    }

    /**
     * 入力をもとに漢字とカナが一致しているかどうか判定，出力をWeb用に
     * @param line　漢字とカナ，その他の情報が行で入っている
     * @param kanji_idx　漢字姓名がline内で入っている位置，０オリジン
     * @param kana_idx　カナ姓名がline内で入っている位置，０オリジン
     * @param separator　lineのセパレータ，
     * @return 結果データ
     * @throws Exception 一般的なエラー
     */
    public Output exec(String line, int kanji_idx, int kana_idx, String separator) throws Exception{
        logger.debug("line="+line);
        ModelData md = run(line, kanji_idx, kana_idx, separator);
        return OutputMaker.exec(md);
    }
}
