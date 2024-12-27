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

package jp.go.digital.kanjikana.core.executor.match.strategy.impl;

import jp.go.digital.kanjikana.core.executor.match.strategy.AbstStrategy;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.model.impl.AsIsWordModel;
import jp.go.digital.kanjikana.core.model.impl.CrawlOSSWordModel;
import jp.go.digital.kanjikana.core.model.impl.DictWordModel;
import jp.go.digital.kanjikana.core.model.impl.ICrawlOSSWordModel;
import jp.go.digital.kanjikana.core.model.impl.IDictCharModel;
import jp.go.digital.kanjikana.core.model.impl.IDictWordModel;
import jp.go.digital.kanjikana.core.model.impl.AiWordModel;
import jp.go.digital.kanjikana.core.model.impl.AsIsCharModel;
import jp.go.digital.kanjikana.core.model.impl.DictCharModel;
import jp.go.digital.kanjikana.core.model.impl.FCharModel;
import jp.go.digital.kanjikana.core.model.impl.FWordModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * 詳細モデルを，シングルトンで定義する
 * @deprecated 信頼性の高いモデルと低いモデルがごっちゃになっているため使用禁止
 */
public final class StrategyAdvanced extends AbstStrategy {
    private static StrategyIF sb=null;
    private final Logger logger = LogManager.getLogger(StrategyAdvanced.class);

    private StrategyAdvanced() throws Exception{
        super(Arrays.asList(new AsIsWordModel(),new AsIsCharModel(), new CrawlOSSWordModel(), new ICrawlOSSWordModel(), new DictWordModel(), new FWordModel(), new DictCharModel(), new FCharModel(),  new IDictWordModel(), new IDictCharModel(),new AiWordModel()));
    }

    /**
     * 詳細モデルを返す
     * @return 詳細モデル
     * @throws Exception 一般的なエラー
     */
    public synchronized static StrategyIF newInstance() throws Exception{
        if(sb==null){
            sb = new StrategyAdvanced();
        }
        return sb;
    }
}