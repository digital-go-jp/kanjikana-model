# core
漢字カナ突合モデルのコアとなるライブラリ・実行ファイルを格納している

## 実行環境
本プログラムの実行は次の環境で確認している。

- Redhat EL 8, MacOS 15
- jdk 17
- maven 3
- Python 3.11


## 機能
本モジュールは，次の機能を有する
- 漢字カナ突合    
漢字・アルファベット姓名とカナ姓名を入力し，一致しているかどうかを判定する
- カタカナ推計    
漢字・アルファベット姓名を入力し，その漢字姓名から推測される読みのカタカナを出力する
- 漢字・アルファベット推計    
カタカナ姓名を入力し，そのカタカナ姓名から推測される漢字・アルファベット姓名を出力する



## downloading library
実行時にDJLライブラリをダウンロードするため、インターネット環境がないときにはエラーとなる
インターネットに接続できない環境で実行する際には，あらかじめインターネット接続環境で実行しておき，.djl.aiディレクトリを実行するユーザのホームディレクトリ以下にコピーしておくこと。

## コンパイル
### 事前準備
pom.xmlのバージョン番号を必要に応じて書き換えておく。Jarファイルにもバージョン番号が付与される
```xml
    <groupId>jp.go.digital.kanjikana</groupId>
    <artifactId>kanjikana_core_oss</artifactId>
    <version>1.6o</version>   <!-- ここを書き換える -->

```

### javadoc作成
mavenコマンドを実行しJavadocを作成する。[javadoc/apidocs以下](./javadoc/apidocs/index.html)に作成される
```bash
mvn javadoc:javadoc
```


### jarファイル作成
mavenコマンドを実行しJarファイルを作成する。target以下に関連ファイルをすべて取り込んだJarファイルが作成される
```bash
mvn compile assembly:single
```

## 実行

### 入力データ仕様
[入力データ仕様](./DATA.md)を参照のこと。


### 入力ファイル

   ```
   1,山田　太郎,ヤマダ　タロウ
   2,田中［山田］　花子,ヤマダ　ハナコ
   3,Marty McFly＿回到　未来（田中　五郎）,マーティー　マクフライ
   ```
## 漢字カナ突合実行
### 実行コマンド
   ```
   !java -Xmx8196M -Dlog4j.configurationFile=log4j2.xml -classpath lib/kanjikana_core-$VER-jar-with-dependencies.jar jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain --infile input.txt --outfile outfile.txt --kanji_idx 1 --kana_idx 2 --strategy ENSEMBLE --has_header true --thread_num 1 --sep csv
   ```
   - VMオプション
      - -Xmx4096M     
         JavaVMに4Gバイト以上のメモリを与える。これ未満での動作は保証しておらず，OutOfMemoryエラーが出る。
      - -Dlog4j.configureationFile    
         本プログラムでは内部のロッギングにLog4jを使用している。ログ出力する際にはlog4j2.xmlを作成し，指定すること。
      - -classpath    
        Jarファイルまでのパスを記述する。なお，環境変数CLASSPATHにJarファイルまでのパスを記載した場合には本項目不要である。

   - プログラムオプション
      - --infile（デフォルト：input.txt）          
        入力ファイル名を指定すること。
      - --outfile（デフォルト：output.txt）    
        出力ファイル名を指定すること。
      - --kanji_idx（デフォルト：1）    
        入力ファイル内における，漢字・アルファベット姓名の列の位置を指定する。１列目は0を指定し，以下，2列目は１という形で，指定すること。
      - --kana_idx（デフォルト：2）    
        入力ファイル内における，カタカナ姓名の列の位置を指定する。１列目は0を指定し，以下，2列目は１という形で，指定すること。
      - --sep（デフォルト：csv）    
        入力ファイル及び出力ファイルの列の区切り文字。csvかtsvを指定する。
      - --thread_num（デフォルト：1）    
        大量入力データなどの時に，計算を多重化して行う場合には2以上の値を指定する。CPUの数以下を推奨。1を設定した場合には多重化処理を行わない。なお，1超の値を入力した際には，出力ファイル名の末尾に".数字"がついたファイルが，thread_numで指定した数だけ作成される。
      - --has_header（デフォルト：true）    
        入力ファイルの１行目をヘッダ行の場合にはtrue，１行目のデータの場合にはfalseを指定する。trueを指定した際には，出力ファイルの１行目にヘッダ行が出力される。
      - --strategy（デフォルト：ENSEMBLE）    
        氏名漢字カナ突合に使用する，ストラテジを指定する。ストラテジの詳細は [氏名漢字カナ突合システムサポートサイト](https://kktg.digital.go.jp/support/index.html)を参照のこと。
      
        
        |ストラテジ    |  内容                                                                   |
        |------------|-------------------------------------------------------------------------| 
        |BASIC       |  信頼度の高い辞書を用いて判定                                                |
        |ENSEMBLE    |  BASICを実行し，NGとなったものに対して，信頼度の高くないモデル(AI,辞書,統計)で多数決|
        |ONLY_AI     |  ENSEMBLE内のAIモデルのみで判定                                             |
        |ONLY_DICT   |  ENSEMBLE内の辞書モデルのみで判定                                            |
        |ONLY_STAT   |  ENSEMBLE内の統計モデルのみで判定                                           |

### 出力
#### 出力１（簡易モデルでOKの場合）
```
8,東京［日本］　花子,トウキョウ　ハナコ,ok,true,90,{true;東京;トウキョウ;WordEngine;DictOSS;0:/canna/freewnn/kakasi/;null}{true;花子;ハナコ;WordEngine;DictOSS;0:/canna/freewnn/kakasi/skk/;null},2025-01-15 09:39:43.148 +0900,2025-01-15 09:39:43.148 +0900
```
- 各項目の説明

  - 8,東京［日本］　花子,トウキョウ　ハナコ,ok

      入力ファイルと同じ

  - true

      漢字とカナが一致したかどうかをtrue/falseで記載される。次項が50以上の場合にはtrue，それ未満はfalseとなる。

  - 90

      漢字とカナの判定結果が記載される。0-99の数値を取る。50以上が一致と判定され，数値が大きいほど一致度が高いことを意味する。

  - {true;東京;トウキョウ;WordEngine;DictOSS;0:/canna/freewnn/kakasi/;null}{true;花子;ハナコ;WordEngine;DictOSS;0:/canna/freewnn/kakasi/skk/;null}

      漢字とカナの一致判定の理由が記載される。

    - フォーマット


        ```
        {簡易モデルでの結果}{簡易モデルでの結果}..      
        ```


      結果は単語単位で{}内に作成される

        ```
         {簡易モデルでの結果} [<辞書モデルでの結果> <統計モデルでの結果><AIモデルでの結果>]  
        ```


    - 簡易モデルでの結果

      単語単位に分割し，それぞれの漢字・アルファベットとカタカナの一致を判断している。
    
        ```
         {true;東京;トウキョウ;WordEngine;DictOSS;0:/canna/freewnn/kakasi/;null}
        ```

      ;区切りの各項目の説明は以下の通り。

      - true

        判定結果，東京とトウキョウが一致している

      - 東京

        入力した漢字

      - トウキョウ

        入力したカタカナ

      - WordEngine
      
        東京とトウキョウをtrueと判定したモデルのクラス名

      - DictOSS

        WordEngineでtrueと判定した辞書を保持するクラス名

      - 0

        必ず0が記載される。

      - /canna/freewnn/kakasi/

        一致と判定した辞書の由来が"/"区切りで記載される。


      - null
    
        必ずnull
  
  - 2025-01-15 09:39:43.148 +0900

      このレコードの計算開示時間を記載している。

  - 2025-01-15 09:39:43.148 +0900

      このレコードの計算終了時間を記載している


#### 出力2（簡易モデル，外国人モデルでOKの場合）
この入力では，ALLEN JACKSONとアーレン　ジャクソンのペアで一致と判定されている。

```
14,ALLEN JACKSON＿鈴木　一郎（高橋　二郎）,アーレン　ジャクソン,ok,true,90,{true;ＡＬＬＥＮ;アーレン;FWordEngine;;0:/;0000000}{true;ＪＡＣＫＳＯＮ;ジャクソン;FWordEngine;;0:/;0000000},2025-01-15 09:39:49.807 +0900,2025-01-15 09:39:49.816 +0900
```

  各項目の説明

  - 14,ALLEN JACKSON＿鈴木　一郎（高橋　二郎）,アーレン　ジャクソン,ok

      入力ファイルと同じ

  - true

      漢字とカナが一致したかどうかをtrue/falseで記載される。次項が50以上の場合にはtrue，それ未満はfalseとなる。

  - 90

      漢字とカナの判定結果が記載される。0-99の数値を取る。50以上が一致と判定され，数値が大きいほど一致度が高いことを意味する。

  - {true;ＡＬＬＥＮ;アーレン;FWordEngine;;0:/;0000000}{true;ＪＡＣＫＳＯＮ;ジャクソン;FWordEngine;;0:/;0000000}

      漢字とカナの一致判定の理由が記載される。

    - フォーマット

        ```
        {簡易モデルでの結果}{簡易モデルでの結果}..      
        ```


      結果は単語単位で{}内に作成される

        ```
         {簡易モデルでの結果} [<辞書モデルでの結果> <統計モデルでの結果><AIモデルでの結果>]  
        ```


    - 簡易モデルでの結果

      単語単位に分割し，それぞれの漢字・アルファベットとカタカナの一致を判断している。
    
        ```
         {true;ＡＬＬＥＮ;アーレン;FWordEngine;;0:/;0000000}
        ```


      ;区切りの各項目の説明は以下の通り。

      - true

        判定結果，ＡＬＬＥＮとアーレンが一致している

      - ＡＬＬＥＮ

        入力したアルファベット

      - アーレン

        入力したカタカナ

      - FWordEngine
      
        ＡＬＬＥＮとアーレンをtrueと判定したモデルのクラス名

      - ブランク

        FWordEngineでは辞書を使っていないのでブランクとなる

      - 0

        必ず0が記載される。

      - /

        由来はブランクになる。


      - 0000000
    
        外国人モデルが判定した結果。０１のビットで下記表に基づいて出力される。0の場合にはTrue，１の場合にはFalseであり，そのビットの判定において成功したか否かを示している。


        |左からのビット位置     |   判定の説明                                                             |
        |--------------------|------------------------------------------------------------------------| 
        | 1                  |  アルファベット文字列は許された文字のみで構成されているか                        |
        | 2                  |  カタカナ文字列は許された文字のみで構成されているか                             |
        | 3                  |  アルファベット側にイニシャルが含まれていないか                                |
        | 4                  |  カタカナ文字列は規範的カタカナ文字列か                                       |
        | 5                  |  両側の要素数は一致するか                                                   |
        | 6                  |  姓名を逆にしたかどうか　ALLEN JACKSON，ジャクソン　アレン　として判定した場合には１|
        | 7                  |  文字列間に対応がとれるか                                                   |
\

  - 2025-01-15 09:39:49.807 +0900

      このレコードの計算開示時間を記載している。

  - 2025-01-15 09:39:49.816 +0900

      このレコードの計算終了時間を記載している



#### 出力3（詳細モデル）
この例では，簡易モデルでFalseとなったが，詳細モデルの辞書モデルでTrue，AIモデルでFalseとなった例である。
```
3,山田　太郎,サンダ　フトロウ,ok,false,30,{false;山田;サンダ;WordEngine;;0:/;}{false;太郎;フトロウ;WordEngine;;0:/;}[<|DictCharModel|{true;山;サン;CharEngine;DictSeimei;0:/wikipedia/;null}{true;田;ダ;CharEngine;DictTankanji;0:/canna/freewnn/kakasi/mozc/;null}{true;太;フト;CharEngine;DictSeimei;0:/wikipedia/;null}{true;郎;ロウ;CharEngine;DictSeimei;0:/wikipedia/;null}><|AiCharModel|{true;山田;サン>ダ;AiWordEngine;;0:/;rank:1;Probability:0.13658617050112723}{false;太郎;フトロウ;AiCharEngine;;0:/;}>],2025-01-15 09:39:41.463 +0900,2025-01-15 09:39:41.470 +0900
```

  各項目の説明

  - 3,山田　太郎,サンダ　フトロウ,ok

      入力ファイルと同じ

  - false

      漢字とカナが一致したかどうかをtrue/falseで記載される。次項が50以上の場合にはtrue，それ未満はfalseとなる。

  - 30

      漢字とカナの判定結果が記載される。0-99の数値を取る。50以上が一致と判定され，数値が大きいほど一致度が高いことを意味する。

  - ``{false;山田;サンダ;WordEngine;;0:/;}{false;太郎;フトロウ;WordEngine;;0:/;}[<|DictCharModel|{true;山;サン;CharEngine;DictSeimei;0:/wikipedia/;null}{true;田;ダ;CharEngine;DictTankanji;0:/canna/freewnn/kakasi/mozc/;null}{true;太;フト;CharEngine;DictSeimei;0:/wikipedia/;null}{true;郎;ロウ;CharEngine;DictSeimei;0:/wikipedia/;null}><|AiCharModel|{true;山田;サンダ;AiWordEngine;;0:/;rank:1;Probability:0.13658617050112723}{false;太郎;フトロウ;AiCharEngine;;0:/;}>]``

      漢字とカナの一致判定の理由が記載される。

    - フォーマット

        ```
        {簡易モデルでの結果} [<辞書モデルでの結果> <統計モデルでの結果><AIモデルでの結果>]    
        ```


      結果は単語単位で{}内に作成される


    - 簡易モデルでの結果

      単語単位に分割し，それぞれの漢字・アルファベットとカタカナの一致を判断している。
    
        ```
         {false;山田;サンダ;WordEngine;;0:/;}
        ```

      ;区切りの各項目の説明は以下の通り。

      - false

        判定結果，山田とサンダが一致していない

      - 山田

        入力した漢字

      - サンダ

        入力したカタカナ

      - WordEngine
      
        東京とトウキョウをfalseと判定したモデルのクラス名。WordEngineはスペース区切りの単語単位で検査する

      - ブランク

        falseとなっているので辞書はブランクとなる

      - 0

        必ず0が記載される。

      - /

        一致していないので，辞書の由来は"/"のみ。

    - 詳細モデルでの結果
      
      詳細モデルでは，辞書モデルと，AIモデルの２つで同時に評価し，多数決で評価判定している。本プログラムでは，２つの多数決となるので，２つともTrueと判定したときにはTrue,それ以外はFalseとなる。

      結果のフォーマットは [<辞書モデルでの結果><AIモデルでの結果>]となっている

        ```
        [<|DictCharModel|{true;山;サン;CharEngine;DictSeimei;0:/wikipedia/;null}{true;田;ダ;CharEngine;DictTankanji;0:/canna/freewnn/kakasi/mozc/;null}{true;太;フト;CharEngine;DictSeimei;0:/wikipedia/;null}{true;郎;ロウ;CharEngine;DictSeimei;0:/wikipedia/;null}><|AiCharModel|{true;山田;サン>ダ;AiWordEngine;;0:/;rank:1;Probability:0.13658617050112723}{false;太郎;フトロウ;AiCharEngine;;0:/;}>]
        ```

      - 辞書モデルでの結果

        単語単位に分割し，それぞれの漢字・アルファベットとカタカナの一致を判断している。
    
           ```
           <|DictCharModel|{true;山;サン;CharEngine;DictSeimei;0:/wikipedia/;null}{true;田;ダ;CharEngine;DictTankanji;0:/canna/freewnn/kakasi/mozc/;null}{true;太;フト;CharEngine;DictSeimei;0:/wikipedia/;null}{true;郎;ロウ;CharEngine;DictSeimei;0:/wikipedia/;null}>
           ```


        - ``|DictCharModel|``

          辞書モデルを示している。
          以下，{}で単語単位，文字単位での漢字・アルファベットとカタカナの突合結果が格納される。

        - {true;山;サン;CharEngine;DictSeimei;0:/wikipedia/;null}
          
          ;区切りの各項目の説明は以下の通り。

          - true

            判定結果，山とサンが一致している

          - 山

            入力した単語を一文字単位で順次抜き出して突合する際に，抽出した文字

          - サン

            入力したカタカナを一文字単位で順次抜き出して突合する際に，抽出した文字

          - CharEngine

            山とサンをtrueと判定したモデルのクラス名，CharEngineは一文字ずつ抜き出して検査する。

          - 0

            必ず0が記載される。

          - /wikipedia/

            山とサンが一致した，辞書の由来。

          - null

            必ずnull

      - AIモデルでの結果

        単語単位に分割し，それぞれの漢字・アルファベットとカタカナの一致を判断している。

           ```
           <|AiCharModel|{true;山田;サンダ;AiWordEngine;;0:/;rank:1;Probability:0.13658617050112723}{false;太郎;フトロウ;AiCharEngine;;0:/;}>
           ```

        - ``|AiCharModel|``

          AIモデルを示している。
          以下，{}で単語単位，文字単位での漢字・アルファベットとカタカナの突合結果が格納される。


        - {true;山田;サンダ;AiWordEngine;;0:/;rank:1;Probability:0.13658617050112723}

          ;区切りの各項目の説明は以下の通り。

          - true

            山田とサンダが一致している。

          - 山田

            入力した漢字

          - サンダ

            入力したカタカナ

          - AiWordEngine

            山田とサンダをtrueと判定したモデルのクラス名，AiWordEngineは単語単位で，AIモデルで検査している

          - 0

            常に０

          - rank:1
          
            AIモデルで，漢字・アルファベットからカタカナを推計した際に，サンダが何番目に確率が高いか

          - Probability:0.13658617050112723

            AIモデルで，漢字・アルファベットからカタカナを推計した際に，サンダの確率



  - 2025-01-15 09:39:41.463 +0900

      このレコードの計算開示時間を記載している。

  - 2025-01-15 09:39:41.470 +0900

      このレコードの計算終了時間を記載している


## カタカナ推計    
### 実行コマンド
```
java -Xmx4096M -Dlog4j.configurationFile=path/to/log4j2.xml  -classpath path/to/shimei_kanjikana_core_oss-1.0-jar-with-dependencies.jar jp.go.digital.kanjikana.core.executor.generate.Kanji2KanaMain --infile path/to/inputfile --outfile path/to/okfile --kanji_idx 2 --sep csv --has_header true 

```
  - VMオプション

    - -Xmx4096M

      JavaVMに4Gバイト以上のメモリを与える。これ未満での動作は保証しておらず，OutOfMemoryエラーが出る。

    - -Dlog4j.configureationFile

      本プログラムでは内部のロッギングにLog4jを使用している。ログ出力する際にはlog4j2.xmlを作成し，指定すること。

      なおサンプルは `log4j2.xml <https://github.com/digital-go-jp/kanjikana-model/core/log4j2.xml>`_ に記載している。

    - classpath

      Jarファイルまでのパスを記述する。なお，環境変数CLASSPATHにJarファイルまでのパスを記載した場合には本項目不要である。

  - プログラムオプション
  
    - --infile（デフォルト：input.txt）
      
      入力ファイル名を指定すること。

    - --outfile（デフォルト：output.txt）

      出力ファイル名を指定すること。

    - --kanji_idx（デフォルト：2）

      入力ファイル内における，漢字・アルファベット姓名の列の位置を指定する。１列目は0を指定し，以下，2列目は１という形で，指定すること。

    - --sep（デフォルト：csv）

      入力ファイル及び出力ファイルの列の区切り文字。csvかtsvを指定する。

    - --has_header（デフォルト：true）

      入力ファイルの１行目をヘッダ行の場合にはtrue，１行目のデータの場合にはfalseを指定する。trueを指定した際には，出力ファイルの１行目にヘッダ行が出力される。

    - --n_best（デフォルト：5）

      カタカナの候補を幾つ出力するか

### 出力
```
7,東京［日本］　花子,ニッポン　ハナコ,ok,kanji:東京［日本］　花子;kanji1:東京　花子;best:1;predict:トウキョウハナコ;probability:-0.0029718216878316236,2025-01-17 14:55:20.437 +0900,2025-01-17 14:55:21.256 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kanji:東京［日本］　花子;kanji1:東京　花子;best:2;predict:アズマキョウハナコ;probability:-6.633387157758405,2025-01-17 14:55:20.437 +0900,2025-01-17 14:55:21.256 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kanji:東京［日本］　花子;kanji1:東京　花子;best:3;predict:トウキョウバナコ;probability:-7.977571781708924,2025-01-17 14:55:20.437 +0900,2025-01-17 14:55:21.256 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kanji:東京［日本］　花子;kanji1:東京　花子;best:4;predict:トウキョウハナゴ;probability:-8.012254843798022,2025-01-17 14:55:20.437 +0900,2025-01-17 14:55:21.256 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kanji:東京［日本］　花子;kanji1:東京　花子;best:5;predict:トウキョウカシ;probability:-8.050246169283813,2025-01-17 14:55:20.437 +0900,2025-01-17 14:55:21.256 +0900
```
各項目の説明（，区切り）

  - 8,東京［日本］　花子,トウキョウ　ハナコ,ok

      入力ファイルと同じ

  - kanji:東京［日本］　花子;kanji1:東京　花子;best:1;predict:トウキョウハナコ;probability:-0.0029718216878316236

     フォーマットは キー１:値１;キー２:値２..  のように，各項目のキーと値を：で区切り，キーと値の組み合わせを;で繋いだ形である。
    

    ;区切りの各項目の説明は以下の通り。

    - kanji;東京［日本］　花子

      入力された漢字・アルファベット姓名

    - kanji1;東京　花子

      kanji1はkanjiを旧姓と分割した際に，東京　花子と日本　花子に分かれるので，kanji1で東京　花子，kanji2で日本　花子を入力として実行する

    - best:1

      東京　花子を入力として，カタカナを推計した際に，n_best個，確率の高い方から出力する。best1からbest5まで確率が高い順に出力される

    - predict:トウキョウハナコ

      東京　花子の入力に対して，AIモデルが，トウキョウハナコと推測した

    - probability:-0.0029718216878316236

      トウキョウハナコと推測された確率。exp(probability)で示され０から１の値を取る。


  - 2025-01-17 14:55:20.437 +0900

      このレコードの計算開示時間を記載している。

  - 2025-01-17 14:55:21.256 +0900

      このレコードの計算終了時間を記載している

## 漢字・アルファベット推計 
### 実行コマンド
```
java -Xmx4096M -Dlog4j.configurationFile=path/to/log4j2.xml  -classpath path/to/shimei_kanjikana_core_oss-1.0-jar-with-dependencies.jar jp.go.digital.kanjikana.core.executor.generate.Kana2KanjiMain --infile path/to/inputfile --outfile path/to/okfile --kana_idx 1 --sep csv --has_header true --n_best 5
```  
- VMオプション

    - -Xmx4096M

      JavaVMに4Gバイト以上のメモリを与える。これ未満での動作は保証しておらず，OutOfMemoryエラーが出る。

    - -Dlog4j.configureationFile

      本プログラムでは内部のロッギングにLog4jを使用している。ログ出力する際にはlog4j2.xmlを作成し，指定すること。

      なおサンプルは `log4j2.xml <https://github.com/digital-go-jp/kanjikana-model/core/log4j2.xml>`_ に記載している。

    - classpath

      Jarファイルまでのパスを記述する。なお，環境変数CLASSPATHにJarファイルまでのパスを記載した場合には本項目不要である。

  - プログラムオプション
  
    - --infile（デフォルト：input.txt）
      
      入力ファイル名を指定すること。

    - --outfile（デフォルト：output.txt）

      出力ファイル名を指定すること。

    - --kana_idx（デフォルト：2）

      入力ファイル内における，カタカナ姓名の列の位置を指定する。１列目は0を指定し，以下，2列目は１という形で，指定すること。

    - --sep（デフォルト：csv）

      入力ファイル及び出力ファイルの列の区切り文字。csvかtsvを指定する。

    - --has_header（デフォルト：true）

      入力ファイルの１行目をヘッダ行の場合にはtrue，１行目のデータの場合にはfalseを指定する。trueを指定した際には，出力ファイルの１行目にヘッダ行が出力される。

    - --n_best（デフォルト：5）

      漢字・アルファベットの候補を幾つ出力するか

### 出力
```
7,東京［日本］　花子,ニッポン　ハナコ,ok,kana:ニッポン　ハナコ;best:2;predict:日本華子;probability:-1.1956451967940704,2025-01-17 14:41:31.519 +0900,2025-01-17 14:41:32.085 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kana:ニッポン　ハナコ;best:3;predict:ニッポン花子;probability:-4.118305028074197,2025-01-17 14:41:31.519 +0900,2025-01-17 14:41:32.085 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kana:ニッポン　ハナコ;best:4;predict:にっぽん花子;probability:-4.402838319025599,2025-01-17 14:41:31.519 +0900,2025-01-17 14:41:32.085 +0900
7,東京［日本］　花子,ニッポン　ハナコ,ok,kana:ニッポン　ハナコ;best:5;predict:日本ハナコ;probability:-4.64612681420105,2025-01-17 14:41:31.519 +0900,2025-01-17 14:41:32.085 +0900
```

各項目の説明（，区切り）

  - 8,東京［日本］　花子,トウキョウ　ハナコ,ok

      入力ファイルと同じ

  - kana:ニッポン　ハナコ;best:2;predict:日本華子;probability:-1.1956451967940704

     フォーマットは キー１:値１;キー２:値２..  のように，各項目のキーと値を：で区切り，キーと値の組み合わせを;で繋いだ形である。
    

    ;区切りの各項目の説明は以下の通り。

    - kana:ニッポン　ハナコ

      入力された漢字・アルファベット姓名

    - kanji1;東京　花子

      kanji1はkanjiを旧姓と分割した際に，東京　花子と日本　花子に分かれるので，kanji1で東京　花子，kanji2で日本　花子を入力として実行する

    - best:1

      東京　花子を入力として，カタカナを推計した際に，n_best個，確率の高い方から出力する。この項目は何番目に高い確率を示す。なおbestが1番目からn_best番目まで，確率が高い順に出力される

    - predict:日本華子

      ニッポン　ハナコから推測された漢字が日本華子である

    - probability:-1.1956451967940704

      日本華子と推測された確率。exp(probability)で示され０から１の値を取る。

  - 2025-01-17 14:41:31.519 +0900

      このレコードの計算開示時間を記載している。

  - 2025-01-17 14:41:32.085 +0900

      このレコードの計算終了時間を記載している

