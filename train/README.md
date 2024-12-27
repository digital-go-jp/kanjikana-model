# AIモデル訓練用スクリプト

datasetのデータを用いて，漢字姓名（外国人の場合にはアルファベット姓名）とカナ姓名をtransformerのSeq2Seqで学習し，漢字姓名からカナ姓名，もしくは，カナ姓名から漢字姓名を推測する。


## transformer_model.py
モデルを作成する

## generate_batch.py
推論を行う

## scripted.py
javaのDJLで読み込めるようにモデルの変換を行う

---
# model

oss model 20241212

ver1.6oで使用

- ai
```
合計 689988
-rw-r--r-- 1 analysis01 analysisgroup 134625143 12月  9 13:52 decoder.pt
-rw-r--r-- 1 analysis01 analysisgroup 100965685 12月  9 13:52 encoder.pt
-rw-r--r-- 1 analysis01 analysisgroup    216217 12月  9 13:52 generator.pt
-rw-r--r-- 1 analysis01 analysisgroup       285 12月  9 13:52 params.json
-rw-r--r-- 1 analysis01 analysisgroup  10245557 12月  9 13:52 positional_encoding.pt
-rw-r--r-- 1 analysis01 analysisgroup 353111831 12月  9 13:52 script.pt
-rw-r--r-- 1 analysis01 analysisgroup 106903187 12月  9 13:52 src_tok_emb.pt
-rw-r--r-- 1 analysis01 analysisgroup    221019 12月  9 13:52 tgt_tok_emb.pt
-rw-r--r-- 1 analysis01 analysisgroup    236177 12月  9 13:52 vocab_src.txt
-rw-r--r-- 1 analysis01 analysisgroup       418 12月  9 13:52 vocab_tgt.txt

```
- ai\_r
```
合計 898760
-rw-r--r-- 1 analysis01 analysisgroup 134625143 12月 12 14:33 decoder.pt
-rw-r--r-- 1 analysis01 analysisgroup 100965685 12月 12 14:33 encoder.pt
-rw-r--r-- 1 analysis01 analysisgroup 107107033 12月 12 14:33 generator.pt
-rw-r--r-- 1 analysis01 analysisgroup       285 12月 12 14:33 params.json
-rw-r--r-- 1 analysis01 analysisgroup  10245557 12月 12 14:33 positional_encoding.pt
-rw-r--r-- 1 analysis01 analysisgroup 460002583 12月 12 14:33 script.pt
-rw-r--r-- 1 analysis01 analysisgroup    220819 12月 12 14:33 src_tok_emb.pt
-rw-r--r-- 1 analysis01 analysisgroup 106903451 12月 12 14:33 tgt_tok_emb.pt
-rw-r--r-- 1 analysis01 analysisgroup       418 12月 12 14:33 vocab_src.txt
-rw-r--r-- 1 analysis01 analysisgroup    236177 12月 12 14:33 vocab_tgt.txt
```

- dict
```
合計 259064
-rw-r--r-- 1 analysis01 analysisgroup         3 12月 12 14:43 crawl.json
-rw-r--r-- 1 analysis01 analysisgroup    202821 11月 29 17:42 itaiji.json
-rw-r--r-- 1 analysis01 analysisgroup 246289624 12月  5 15:30 oss.json
-rw-r--r-- 1 analysis01 analysisgroup  10097715 12月  9 13:49 seimei.json
-rw-r--r-- 1 analysis01 analysisgroup         3 12月 12 14:43 statistics.json
-rw-r--r-- 1 analysis01 analysisgroup   8673785 12月 12 14:43 tankanji.json

```
