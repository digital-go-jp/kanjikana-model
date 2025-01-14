プログラム
##########

  `Github <https://github.com/digital-go-jp/kanjikanaa-model/>`_ に格納されているプログラムの実装について解説する。

  実際にコマンドラインからの動作を確認する場合には :doc:`command` を参照のこと。



動作環境
=======

  本プログラムは次の動作環境で確認している

  - RedHat Linux Enterprise 8
  - OpenJDK17
  - maven 3


JavaDoc
========
  プログラムのJavaDocは `こちら <../apidocs/index.html>`_ に格納されている。

構成
====

  :doc:`architecture` で定義されている，ストラテジ，モデル，エンジン及び，入口となる実行用クラスに関しての実装については次のとおりである。

  ============= ========================
  　             パッケージ
  ============= ========================
  ストラテジ      jp.go.digital.kanjikana.core.executor.match.strategy
  モデル         jp.go.digital.kanjikana.core.model
  エンジン        jp.go.digital.kanjikana.core.engine
  実行用クラス    jp.go.digital.kanjikana.core.executor
  ============= ========================


ストラテジ
========

  本プログラムで実装しているストラテジは次のとおりである。

  - 簡易モデル

    簡易モデルは，信頼度の高い辞書等の元データを用いて，信頼度の高い突合方法によって，漢字・アルファベットとカタカナの一致判定を行うもの。

    - 概要図

       使用している，辞書等のデータと，エンジンとの関係は次の図の通り。

       .. figure:: img/model_simple.png


    - アルゴリズム
       
       次のマッチング，異体字，突合方法を用いた，アルゴリズムで判定を行っている

      .. figure:: img/strategy_simple.png



  - 詳細モデル

    詳細モデルは，簡易モデルを実行したのちに，漢字・アルファベットとカタカナの突合が不一致となったものに対し，簡易モデルで利用した辞書等のデータからは信頼度が落ちるデータを用いた判定を複数行い，多数決で漢字・アルファベットとカタカナが一致しているかどうかを判定する。

    - 概要図

       使用している，辞書等のデータと，エンジンとの関係は次の図の通り。

       .. figure:: img/model_detail.png


    - アルゴリズム

       次のマッチング，異体字，突合方法を用いた，アルゴリズムで判定を行っている

       .. figure:: img/strategy_detail.png



