# AIモデルサンプル


AIモデル(transformer+Seq2Seq)モデルを，Pytorchで学習し，JavaのDjlライブラリを利用して推計するサンプル。


## dataset

サンプルデータとして，Eng-Fraの翻訳データセットをダウンロードし，訓練用，開発用，検証用，に振り分ける。
データは，訓練用，開発用，検証用に８：１：１の割合で分割し，元データをeng,fraのタグをつけて，JSONL形式に整形する。

```json
{"translation": {"eng": "A new difficulty has arisen.", "fra": "Une nouvelle difficulté est apparue."}}
```

詳細は，[dataset.ipynb](dataset/dataset.ipynb)を参照のこと


## training

Pythonでdatasetで取得したデータを学習するためのプログラム。それぞれの入出力は，スペース区切りに分割し，

## inference

trainingで学習したモデルを用いて，推論を行うプログラム。PythonとDjlを用いて作成したJavaプログラムを格納。


