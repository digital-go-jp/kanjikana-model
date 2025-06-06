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

package jp.go.digital.kanjikana.core.engine.dict;

import static org.hamcrest.Matchers.equalTo;

import jp.go.digital.kanjikana.core.Resources;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class DictTest {

    static class TestDict extends Dict {
        public TestDict(String resource, boolean normalized) throws Exception {
            super(resource, normalized);
        }
    }


    @Test
    public void test2() throws Exception{

        String fname=Resources.getProperty(Resources.PropKey.DIC_RELIABLE);
        Dict dict = new TestDict(fname,true);
        //var map = dict.getDictMap();
        assertThat(dict.containsKey("東京"),equalTo(true));
        assertThat(dict.containsValueKey("東京","トウキヨウ"),equalTo(true));
        assertThat(dict.containsValueKey("東京","トウキョウ"),equalTo(false));
    }

    @Test
    public void test3() throws Exception{
        String fname=Resources.getProperty(Resources.PropKey.DIC_RELIABLE);
        Dict dict = new TestDict(fname,true);
        dict.save("/tmp/dict_freq_normalized.json");
    }

}