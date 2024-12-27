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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * ファイルの漢字姓名とカタカナ姓名を読み込む
 * バッチ処理用
 */
public final class FileReader {
    private final boolean hasHeader;

    /**
     * ファイルを読み込む
     * @param hasHeader　先頭行がヘッダかどうか。ヘッダがない場合にはFalse
     */
    public FileReader(boolean hasHeader){
        this.hasHeader = hasHeader;
    }

    private File getFile(String filename,int suffix){
        if(filename==null || filename.isEmpty()){
            return null;
        }
        return new File(filename+"."+suffix);
    }

    /**
     * ファイル名からFileへ変換
     * @param filename　ファイル名
     * @return filenameのFileオブジェクト
     */
    public File getFile(String filename){
        if(filename==null || filename.isEmpty()){
            return null;
        }
        return new File(filename);
    }

    /**
     * ヘッダを取得する
     * @param infile ファイル名
     * @return ヘッダの文字列CSV
     * @throws Exception 一般的なエラー
     */
    public String getHeader(String infile) throws Exception {
        if(!hasHeader){
            return null;
        }
        if(infile.endsWith(".gz")){
            return getZipText(infile,true).get(0);
        }else {
            File file = new File(infile);
            return getPlainText(file,true).get(0);
        }
    }

    /**
     * ファイルの内容を取得する
     * @param infile ファイル名
     * @return ファイルの各行をリストで取得
     * @throws Exception 一般的なエラー
     */
    public List<String> getFileText(String infile) throws Exception{
        if(infile.endsWith(".gz")){
            return getZipText(infile,false);
        }else {
            File file = new File(infile);
            return getPlainText(file,false);
        }
    }

    private List<String> getZipText(String infile,boolean isHeader) throws Exception{
        List<String> lines = new ArrayList<>();
        GZIPInputStream zin = new GZIPInputStream(new FileInputStream(infile));
        InputStreamReader is = new InputStreamReader(zin, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(is);
        String text;
        int i =0;
        while ((text = br.readLine()) != null) {
            if (hasHeader && i==0){
                if(isHeader){
                    return new ArrayList<String>(Arrays.asList(text));
                }
                i++;
                continue;
            }else {
                lines.add(text.trim());
            }
            i++;
        }
        br.close();
        return lines;
    }

    private List<String> getPlainText(File file,boolean isHeader) throws Exception{
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        );
        String text;
        int i =0;
        while ((text = br.readLine()) != null) {
            if (hasHeader && i==0){
                if(isHeader){
                    return new ArrayList<String>(Arrays.asList(text));
                }
                i++;
                continue;
            }else {
                lines.add(text.trim());
            }
            i++;
        }
        br.close();
        return lines;
    }

}
