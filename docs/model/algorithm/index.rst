
アルゴリズム
====================

漢字カナ突合モデルのアルゴリズムについて記載する。
モデル内は，エンジン，モデル，ストラテジーの概念がある。

漢字カナモデルはストラテジによって使い分けていく。ストラテジ内にはモデルを複数保持し，モデル内には複数のエンジンが保持される。

.. mermaid::

        graph TD
          漢字姓名とカナ姓名 -->|入力| KanjiKanaModel
          KanjiKanaModel -->|strategy1を使う時| strategy1
          KanjiKanaModel -->|strategy2を使う時| strategy2

          engine1 --> engine2
          engine4 --> engine5
        
          model1 -->|OK/NG| 結果1
          model2 -->|OK| 結果2
          model2 -->|NG| model3
          model3 -->|OK/NG| 結果2
          engine1
          engine2
          engine3
          engine4
          engine5
          subgraph model1
            engine1
            engine2
          end
          subgraph model2
            engine1
            engine3
          end
          subgraph model3
            engine4
            engine5
          end
          subgraph strategy1
            model1
            結果1
          end
          subgraph strategy2
            model2
            model3
            結果2
          end





.. toctree::
   :maxdepth: 1
   :caption: Contents:
   :numbered:

:doc:`./engine/index` と :doc:`./model/index`    
-----------------------------------------------------------------
:doc:`./model/index` は :doc:`./engine/index` を複数組み合わせて，ひつの機能として漢字姓名とカナ姓名の一致を判定するものである。


:doc:`./engine/index` は，OSSの姓名辞書を使って漢字姓名とカナ姓名の一致を判定するものや，外国人アルファベットのモデルを用いて判定するもの，AIを用いて判定するものなど，様々なものが存在する。これらのどれかを用いて，漢字姓名とカナ姓名を入力してその一致を判定する単機能として構築される。

エンジンへの入力は，単語単位の漢字姓名とカナ姓名である。 例えば，漢字姓名として「山田　光宙」，カナ姓名として「ヤマダ　ピカチュウ」という突合を行う際には，:doc:`./model/index` へは，「山田　光宙」と「ヤマダ　ピカチュウ」が入力されるが，エンジンへは，スペース区切りで分割した，「山田」と「ヤマダ」が初回入力され，次に「光宙」と「ピカチュウ」のペアがエンジンへ入力される。

.. 例えば，漢字姓名として「山田　光宙」，カナ姓名として「ヤマダ　ピカチュウ」という入力があった際に，OSSの漢字姓名辞書で一致判定するエンジンを用いた際には，漢字姓の「山田」とカナ姓の「ヤマダ」はOSS辞書にこのペアが存在し一致として判断できるが，「光宙」と「ピカチュウ」のペアがOSS辞書にない場合には，「山田　光宙」と「ヤマダ　ピカチュウ」の入力に対して，OSS姓名辞書エンジンではNGとなる。

.. mermaid::

        graph TD
          input(漢字姓名:山田　光宙\nカナ姓名:ヤマダ　ピカチュウ)
          input --> model1
          e1(engine1\n漢字姓名:山田,カナ姓名:ヤマダ　OK\n漢字姓名:光宙,カナ姓名:ピカチュウ　NG)
          e2(engine2\n漢字姓名:光宙,カナ姓名:ピカチュウ　OK)
          subgraph model1
            e1
            e2
          end
          e1 -->|漢字姓名:光宙\nカナ姓名:ピカチュウ| e2

          model1 -->|OK| 結果1


:doc:`./strategy/index`  
------------------------------------------------
:doc:`./strategy/index` は　:doc:`./model/index`  の特性を考慮し，組み合わせを変更しながら判定する。
例えば，リアルタイム性を重視して，あまり実行時間のかからない:doc:`./model/index`の組み合わせで作成するものや，
信頼性があまり高くない，AIモデルなどを複数用いてアンサンブル学習で結果を判定するものなど，様々なものを提供する。