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

