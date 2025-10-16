# 氏名漢字カナ突合モデルWebAPI

氏名漢字カナ突合モデルをWebAPI形式で使用できるように、SpringBootでWrapしたものです。


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
起動後にブラウザでアクセスするとJSON形式で返却する
（初回の起動は時間がかかります）

```
http://localhost:8080/deitail?kanji=山田　太郎&kana=ヤマダ　タロウ
```

詳細は「別紙　基本設計書（漢字カナ突合モデル）」を参照


## バッチ処理形式でのアクセス

1. CSV形式で作成してください。漢字姓名のフォーマットは
   ```
   1,山田　太郎,ヤマダ　タロウ
   2,田中［山田］　花子,ヤマダ　ハナコ
   3,Marty McFly＿回到　未来（田中　五郎）,マーティー　マクフライ
   ```
1. 
