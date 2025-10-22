# 氏名漢字カナ突合モデル


本プログラムの詳細および使用方法については、[氏名漢字カナ突合システムサポートサイト](https://kktg.digital.go.jp/support/index.html)をご参照ください。

## 説明


氏名漢字カナ突合モデルは、氏名の漢字またはアルファベット表記と、その読みであるカタカナ表記を突合するモデルです。
本モデルは、以下の3つの機能を備えています。

- 氏名漢字カナ突合機能

  漢字またはアルファベット表記の氏名とカタカナ表記の氏名を入力し、それらが一致しているかを判定します。判定結果は0〜100の数値で返され、数値が高いほど一致度が高いことを示します。


- 氏名カタカナ推計機能

  漢字またはアルファベット表記の氏名を入力すると、推定される読みのカタカナ表記を、推定確率の高い順に出力します。

- 氏名漢字推計機能

  カタカナ表記の氏名を入力すると、対応する漢字またはアルファベット表記の氏名を、推定確率の高い順に出力します。


## 使用データ

本モデルでは、漢字・アルファベットとその読みであるカタカナのペアをデータとして使用しています。
使用したデータは以下の通りです。利用にあたっては、各データのライセンスをご確認ください。


### 辞書データ
- [IPAdic](https://canna-input.github.io/)
- [FreeWnn](http://www.tomo.gr.jp/FreeWnn/)
- [IPAdic](https://taku910.github.io/mecab/)
- [KAKASI](http://kakasi.namazu.org/)
- [Mozc](https://github.com/google/mozc/)
- [NEologd](https://github.com/neologd/mecab-ipadic-neologd/)
- [SKK](https://github.com/skk-dev/dict/)

### 異体字データ
- [MJ](https://moji.or.jp/mojikiban/)  「MJ文字情報一覧表は[クリエイティブ・コモンズ 表示 – 継承 2.1 日本 ライセンス条件](https://creativecommons.org/licenses/by-sa/2.1/jp/)で提供されています。」

### Webデータ
- [Wikipedia](https://dumps.wikimedia.org/)


## モデル

各機能のアルゴリズムの詳細については、[モデル](https://kktg.digital.go.jp/support/model/index.html)をご参照ください。

### 辞書モデル

漢字・アルファベットとその読みであるカタカナの辞書を用いて突合を行い、入力された氏名が一致しているかを判定します。
使用する辞書データは、上記「使用データ」からダウンロードし、スクリプトを用いて作成可能です。



詳細は、[dict](./dict/README.md)をご参照ください。


なお、作成した辞書モデルで使用する辞書ファイルは、[氏名突合支援サービス](https://kktg.digital.go.jp/support/resources/index.html)で配布しています。

### 外国人モデル
アルファベット表記の氏名とカタカナ表記の氏名を入力とし、 「日本語（ローマ字表記）–英語表記部分対応表」に基づいて突合を行います。
詳細は以下の文献をご参照ください。

[佐藤 理史. 2020 東京オリンピック参加者名簿の翻訳. 自然言語処理, Vol.30, No.2, pp748-772, 2023.](https://www.jstage.jst.go.jp/article/jnlp/30/2/30_748/_article/-char/ja)

### AIモデル

上記「使用データ」から抽出した漢字・アルファベットとカタカナのペアを用いてデータセットを作成し、Transformerモデルで学習を行います。

学習時には、漢字・アルファベットおよびカタカナを一文字単位で分割し、Transformerに入力・出力として与えます。
詳細は、[train](./train/README.md)をご参照ください。


作成したモデルは以下の2種類です。
- 漢字・アルファベットからカタカナを推論するモデル

  氏名漢字カナ突合機能および氏名カタカナ推計機能で使用します。
  学習方法の詳細については，[training.ipynb](train/training.ipynb)、
  学習済みモデルを用いてのファインチューニングの方法の詳細については、[finetuning.ipynb](train/finetuning.ipynb)をご参照ください。

- カタカナから漢字・アルファベットを推論するモデル

  氏名漢字推計機能で使用します。

  学習方法の詳細は、[training_r.ipynb](train/training_r.ipynb)をご参照ください。


事前学習済みモデルは、[氏名漢字カナ突合システムサポートサイト](https://kktg.digital.go.jp/support/resources/index.html)で配布しています。


## 実行方法

本モデルは、コマンドラインから実行可能なプログラムとして提供されています。
また，[氏名突合支援API](https://api.kktg.digital.go.jp/)の内部でも利用しています。

プログラムの詳細は、[core](./core/README.md)をご参照ください。


## サンプルプログラム


AIモデルはPythonのPyTorchで構築されており、PythonおよびJavaによる推計サンプルを提供しています。


詳細は[sample](./sample/README.md)をご参照ください。

## 参考文献
本モデルに含まれる外国人モデル（アルファベット氏名とカタカナ氏名の突合モデル）は、名古屋大学の佐藤理史教授が作成したRubyプログラムをJavaに移植したものです。
- 佐藤 理史. 2020 東京オリンピック参加者名簿の翻訳. 自然言語処理, Vol.30, No.2, pp748-772, 2023.
[論文](https://www.jstage.jst.go.jp/article/jnlp/30/2/30_748/_article/-char/ja)


