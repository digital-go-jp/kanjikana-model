# AIモデル訓練用スクリプト

datasetのデータを用いて，漢字姓名（外国人の場合にはアルファベット姓名）とカナ姓名をtransformerのSeq2Seqで学習し，漢字姓名からカナ姓名，もしくは，カナ姓名から漢字姓名を推測する。


## transformer_model.py
モデルを作成する

## generate_batch.py
推論を行う

## scripted.py
javaのDJLで読み込めるようにモデルの変換を行う

---
## 20240827
01_prep_new.shを採用

---
# model
