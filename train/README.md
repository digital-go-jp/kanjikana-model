# AIモデル訓練用スクリプト

datasetのデータを用いて，漢字姓名（外国人の場合にはアルファベット姓名）とカナ姓名をtransformerで学習し，漢字姓名からカナ姓名，もしくは，カナ姓名から漢字姓名を推測する。

漢字姓名からカナ姓名を推計するモデルの実行ほう方法は[training.ipynb](./training.ipynb)を，カナ姓名から漢字姓名を推計するモデルの実行方法は[training_r.ipynb](./training_r.ipynb)を参照のこと。


各スクリプトの詳細については下記の通りである。


## 事前準備

- [mergedic.py](./mergedic.py)
  json形式の２つの辞書ファイルをマージする。（同じ漢字とカナのペアがあったものはマージする）

- [prep.py](./prep.py)    
  引数のjsonで指定されるデータを訓練用，開発用，検証用に分ける。開発用と検証用がそれぞれ全体の引数で指定されるratioの率になる

- [catawk.py](./catawk.py)       
  入力されたCSVデータの列位置を指定して，ファイルの行の上位からのスタート位置％とエンド位置％の間のデータを，指定されたファイルに追加する

- [kana.py](./kana.py)    
  ひらがなとカタカナの一文字ずつのペアを作成する

- [jsontotext.py](./jsontotext.py)    
  jsonで作成された辞書データを，CSV形式へ変換する

- [omit_testval.py](./omit_testval.py)    
  開発，検証用に，訓練データに入っているものがあれば，削除する(インサンプルを阻止)

- [shuffle.py](./shuffle.py)    
  漢字・アルファベットとカタカナの２つのファイルを入力しそれぞれの位置がペアとなるようにしたままで，順番をシャッフルする

- [space.py](./space.py)    
  ファイル内のデータを，文字単位でスペース区切りにする

- [format.py](./format.py)
  ２つの漢字・アルファベットとカタカナの入ったファイルから，JSONL形式に変換する


## 学習用スクリプト

- [trainsfer_model.py](./transformer_model.py)    


## モデル変換用