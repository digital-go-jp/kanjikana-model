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

package jp.go.digital.kanjikana.core.engine.dict.impl;

import jp.go.digital.kanjikana.core.engine.AiWordEngine;
import jp.go.digital.kanjikana.core.engine.dict.DictIF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DictOssNormalizedTest {
    DictIF dic;
    private static final Logger logger = LogManager.getLogger(DictOssNormalizedTest.class);

    {
        try {
            dic = DictOSSNormalized.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception{
        List<String> lst = dic.getValue("俊");
        logger.debug(lst);
        assertThat(dic.containsValueKey("俊","シュン"),equalTo(true));
        assertThat(dic.containsValueKey("俊","シユン"),equalTo(true));


    }

    @Test
    public void test2() throws Exception{
        List<String> lst = dic.getValue("田中");
        logger.debug(lst);
        assertThat(dic.containsValueKey("田中","タナカ"),equalTo(true));
        assertThat(dic.containsValueKey("田中","タナヵ"),equalTo(true));
    }
}
