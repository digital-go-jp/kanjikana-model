# AIモデルサンプル


AIモデル(transformer)モデルを，Pytorchで学習し，Python及びJavaのDjlライブラリを利用して推計するサンプル。
各ディレクトリの説明については，次のとおりである。


## dataset

サンプルデータとして，Eng-Fraの翻訳データセットをダウンロードし，訓練用，開発用，検証用，に振り分ける。
データは，訓練用，開発用，検証用に８：１：１の割合で分割し，元データをeng,fraのタグをつけて，JSONL形式に整形する。

```json
{"translation": {"eng": "A new difficulty has arisen.", "fra": "Une nouvelle difficulté est apparue."}}
..
```

詳細は，[dataset.ipynb](dataset/dataset.ipynb)を参照のこと


## training

Pythonでdatasetで取得したデータを学習するためのプログラム。それぞれの入出力の文章は，文字単位で分割しエンコードする。通常，Byte Pair Encoding（SENNRICH, Rico. Neural machine translation of rare words with subword units. arXiv preprint arXiv:1508.07909, 2015.）や，SentencePiece（KUDO, T. Sentencepiece: A simple and language independent subword tokenizer and detokenizer for neural text processing. arXiv preprint arXiv:1808.06226, 2018.）などが使用されるが，本プログラムでは簡単のために，文字単位で分割した。

訓練用データを用いて，Transformerで学習し，１エポックごとに開発用データを用いてLossを計算している。開発用データで最もLossが少なかったモデルファイルをcheckpoint_best.ptとしてモデルファイルを作成した。

学習の詳細については，[training_sample.ipynb](training/training_sample.ipynb)を参照のこと。


## inference_py

trainingで学習したモデルを用いて，PythonのPytorchライブラリを用いて推論を行うプログラム。推論の探索には，Greedyサーチと，Beamサーチを選択できる。

実行結果の詳細については，[inference.ipynb](./inference_py/inference.ipynb)を参照のこと

## inference_java

trainingで学習したモデルを用いて，JavaのDjlライブラリを持ちて推論を行うプログラム。Djl内部では，Pytorchライブラリを使用している。
Djlは，内部で使用しているPytorchライブラリのバージョンを実行する環境で動作するPytorchのバージョンと合わせる必要があるので，[Djl](https://djl.ai/engines/pytorch/pytorch-engine/)のバージョンを適宜変更する日宇町がある。


実行結果の詳細については，[inference_java.ipynb](./inference_java/inference_java.ipynb)を参照のこと。

