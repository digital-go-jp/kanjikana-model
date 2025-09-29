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

import jp.go.digital.kanjikana.core.model.ModelData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class BeamSearchTest {
    private BeamSearch search;
    private BeamSearch search_r;
    {
        try{
            search = new BeamSearch(AiKanjiKanaModels.newInstance());
            search_r = new BeamSearch(AiKanaKanjiModels.newInstance());
            //search = new BeamSearch(new AiKanjiKanaModels());
            //search_r = new BeamSearch(new AiKanaKanjiModels());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        List<SearchResult> res = search.run("高野");
        assertThat(res.get(1).getPredict(),equalTo("コウノ"));
        assertThat(res.get(0).getPredict(),equalTo("タカノ"));
    }

    /*
    @Test
    public void test2(){
        List<SearchResult> res = search_r.run("タカノ");
        assertThat(res.get(0).getPredict(),equalTo("貴乃"));
    }

     */

    @Test
    public void test3() throws Exception{
        List<SearchResult> res = search.run("立臼");
        List<String> ans = new ArrayList<>();
        for(SearchResult s:res){
            ans.add(s.getPredict());
        }
        assertThat(ans, hasItem("タチウス"));


    }
}
