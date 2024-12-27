
アーキテクチャ
=====================

漢字カナ突合モデルは，Javaのモジュール及び，REST形式のAPIとして提供するための，Dockerイメージとして提供される。




.. mermaid::

    stateDiagram-v2
        [*] --> Docker : HTTP REST 8080 port
        Docker --> Tomcat
        Tomcat --> SpringBoot
        SpringBoot --> KanjiKanaModel
        KanjiKanaModel --> resources(Dictionary;AImodels)


KanjiKanaModel
-------------------------
漢字カナライブラリはJavaクラスとして提供される。

ライブラリ内には複数のモデルを保持し，漢字姓名とカナ姓名の一致を調べる際には，順次モデルを適用し，漢字姓名とカナ姓名が一致しているかどうかを判定する。モデルには次のものがある。

姓名辞書モデル
^^^^^^^^^^^^^^
オープンソース等の漢字姓名とカナ姓名のペアを保持した，JSONファイルをresourceとして使用する。入力される漢字姓名から，辞書モデルを用いてカナ姓名と一致する稼働かを検査する（:doc:`/overview/kanji_kana/index`　のときに使用する）


単漢字辞書モデル
^^^^^^^^^^^^^^^^^^^^^^
姓名辞書に存在しないような難読の漢字姓名とカナ姓名の一致を判定するために，単漢字とその読み仮名の辞書を用いて，文字単位で判定するモデルである。




AIモデル
^^^^^^^^^^^^^^^^
オープンソース等の漢字姓名とカナ姓名のペアをもちい，transformerで学習，作成したモデルパラメタをJavaのresouceとして使用する。:doc:`/overview/kanji_kana/index` モデルの一つとして使用する。また，:doc:`/overview/kana/index`　及び :doc:`/overview/kanji/index` のカナ姓名や漢字姓名の推測の際に使用する。