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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.contains;

public class GreedySearchTest {
    private GreedySearch search ;
    {
        try{
            AiModels aiModels = AiKanjiKanaModels.newInstance();
            //AiModels aiModels = new AiKanjiKanaModels();
            search = new GreedySearch(aiModels);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{

        List<SearchResult> res = search.run("高野");

        // "タカノ" が res の中のどれかの getPredict() に含まれているかを検証
        boolean found = res.stream()
                .anyMatch(r -> "タカノ".equals(r.getPredict()));

        assertThat( found, equalTo(true));

    }
}
