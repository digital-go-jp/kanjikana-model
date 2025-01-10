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

package jp.go.digital.kanjikana.core.executor;

import jp.go.digital.kanjikana.core.engine.ai.SearchResult;
import jp.go.digital.kanjikana.core.model.ModelData;
import jp.go.digital.kanjikana.core.model.ModelStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 出力用のOutputクラスを作成するためのクラス
 */
public class OutputMaker {
    public static final String ADDITIONAL_KEY_NOTES = "notes";
    public static final String ADDITIONAL_KEY_PROB = "probability";
    public static final String ADDITIONAL_KEY_STATUS ="status";

    public enum GenKey {
        KANJI("kanji"),
        KANA("kana");
        final String val;

        GenKey(String val) {
            this.val = val;
        }
    }


    public static Output exec(ModelData modelData) throws Exception{
        Output o = new Output();
        o.response = Response.OK;
        o.result = new Result();
        if (modelData.getStatus() != ModelStatus.OK && modelData.getStatus() != ModelStatus.NG) {
            switch (modelData.getStatus()) {
                case E001 -> o.response = Response.E001;
                case E002 -> o.response = Response.E002;
                case E100 -> o.response = Response.E100;
            }
        }

        if (modelData.getEnsembleResults().isEmpty()) {
            if(modelData.getStatus() == ModelStatus.OK ){
                o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.OK);
            }else{
                o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.NG);
            }

        } else {
            int okcnt = 0;
            for (ModelData md : modelData.getEnsembleResults()) {
                if (md.getStatus() == ModelStatus.OK) {
                    okcnt += 1;
                }
            }
            o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS,StatusMatch.getStatus(modelData.getEnsembleResults().size(),okcnt));
            /*
            switch (okcnt) {
                case 0 -> o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.NG);
                case 1 -> o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.ENSEMBLE1);
                case 2 -> o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.ENSEMBLE2);
                case 3 -> o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusMatch.ENSEMBLE3);
            }
             */
        }

        String notes = modelData.getResult();
        o.result.setAdditionalProperty(ADDITIONAL_KEY_NOTES, notes);
        return o;
    }

    public static Output exec(List<SearchResult> res, GenKey generateKey) throws Exception {
        Output o = new Output();

        o.response = Response.OK;
        o.result = new Result();
        if (res == null || res.isEmpty()) {
            o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS,StatusGen.NG);//o.result.status = Status.NG;
            return o;
        }
        List<String> pred = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        for (SearchResult r : res) {
            pred.add(r.getPredict());
            prob.add(Math.pow(10.0, r.getProbability()));
        }
        o.result.setAdditionalProperty(ADDITIONAL_KEY_STATUS, StatusGen.OK);
        o.result.setAdditionalProperty(generateKey.val, pred);
        o.result.setAdditionalProperty(ADDITIONAL_KEY_PROB, prob);
        return o;
    }
}