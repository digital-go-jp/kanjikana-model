# 氏名漢字カナ突合モデルWeb API

氏名漢字カナ突合モデルをWeb API形式で使用できるように、SpringBootでWrapしたものです。


## 起動方法
漢字カナモデルをDockerイメージで提供します。
WindowsやMacで使用する場合には，DockerDesktopをインストールし，起動後に，下記コマンドを実行して立ち上げてください


```
docker build  -t kanjikanaweb:latest .

docker rm kanjikanaweb
docker run --name kanjikanaweb -p 8080:8080 -it kanjikanaweb:latest
```

docker build -t 作成するイメージ名 .
docker run --name 起動名 -p 外部ポート番号:イメージ内部のポート番号8080固定 -it 作成したイメージ名


## REST形式でのアクセス
起動後にブラウザでアクセスするとJSON形式で返却する。
（初回の起動は時間がかかります）

```
http://localhost:8080/deitail?kanji=山田　太郎&kana=ヤマダ　タロウ
```

詳細は[氏名突合支援サービスサポートサイト](https://kktg.digital.go.jp/support/index.html)の「氏名突合支援サービスAPI」を参照願います。