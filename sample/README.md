## AIモデルサンプル


AIモデル(transformer+Seq2Seq)モデルを，Pytorchで学習し，JavaのDjlライブラリを利用して推計するサンプル。


# dataset

サンプルデータとして，Eng-Fraの翻訳データセットをダウンロードし，訓練用，開発用，検証用，に振り分ける

# training

Pythonでdatasetで取得したデータを学習するためのプログラム。

# inference

trainingで学習したモデルを用いて，推論を行うプログラム。PythonとDjlを用いて作成したJavaプログラムを格納している。


