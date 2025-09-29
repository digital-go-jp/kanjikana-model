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

package jp.go.digital.kanjikana.core.engine;

import jp.go.digital.kanjikana.core.engine.dict.impl.DictAsIsNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictReliableNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictTankanjiNormalized;
import jp.go.digital.kanjikana.core.engine.dict.impl.DictUnReliableNormalized;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ICharEngineTest {
    private EngineIF engine;
    {
        try{
            engine = new CharEngine(Arrays.asList( DictAsIsNormalized.newInstance(), DictReliableNormalized.newInstance(), DictUnReliableNormalized.newInstance(),  DictTankanjiNormalized.newInstance()),true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception{
        ResultEngineParts result = engine.check("靑陦","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test4() throws Exception{
        ResultEngineParts result = engine.check("青陦","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }

    @Test
    public void test5() throws Exception{
        ResultEngineParts result = engine.check("靑島","アオシマ");
        assertThat(result.isOk(),equalTo(true));
    }
}
