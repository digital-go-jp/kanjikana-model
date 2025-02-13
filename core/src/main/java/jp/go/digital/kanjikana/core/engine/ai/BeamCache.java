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

package jp.go.digital.kanjikana.core.engine.ai;

import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.utils.LRUCache;

import java.util.List;

/**
 * AIモデルのBeamSearch実行結果をCacheするクラス
 * AIモデルの実行は時間がかかるので，同じものが来た時のためにCacheしておく。さらに，LRUとすることでよくヒットするようにする
 * シングルトンクラスにしておく
 */
class BeamCache {
    private static BeamCache cache=null;
    private final LRUCache<String, List<SearchResult>> lru;

    private BeamCache(){
        lru=new LRUCache<>(Integer.parseInt(Resources.getProperty(Resources.PropKey.AI_PM_CACHE_SIZE)));
    }

    static synchronized BeamCache newInstance(){
        if(cache==null){
            cache = new BeamCache();
        }
        return cache;
    }

    synchronized void put(String key, List<SearchResult> result){
        lru.put(key, result);
    }
    synchronized List<SearchResult> get(String key){
        return lru.get(key);
    }
}
