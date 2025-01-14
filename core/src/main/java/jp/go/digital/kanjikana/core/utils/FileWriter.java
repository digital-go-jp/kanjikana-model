/*
 * MIT License
 *
 * Copyright (c) 2025 デジタル庁
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileWriter {
    private final String fileName;
    public FileWriter(String fileName){
        this.fileName = fileName;
        this.delete();
    }
    public void write(List<String> lines) throws Exception{
        if(this.fileName == null || this.fileName.isEmpty()){
            return;
        }
        //FileWriter fw = new FileWriter(file, is_append);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileName, false), StandardCharsets.UTF_8));

        for(String line:lines){
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
            bw.write(line+ "\n");
        }
        bw.close();
    }

    public void write(String line, boolean is_append) throws Exception{
        if(this.fileName == null || this.fileName.isEmpty()){
            return;
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileName, is_append), StandardCharsets.UTF_8));
        bw.write(line+"\n");
        bw.close();
    }

    public void delete(){
        try {
            Files.delete(Path.of(this.fileName));
        }catch(Exception e){
            // do nothing
        }
    }

}
