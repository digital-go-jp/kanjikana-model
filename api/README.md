# 漢字カナモデル

## 起動方法
漢字カナモデルをDockerイメージで提供します。
WindowsやMacで使用する場合には，DockerDesktopをインストールし，起動後に，下記コマンドを実行して立ち上げてください


```
docker build  -t kanjikanaweb:latest .

docker rm kanjikanaweb
docker run --name kanjikanaweb -p 80:8080 -it kanjikanaweb:latest
```

docker build -t 作成するイメージ名 .
docker run --name 起動名 -p 外部ポート番号:イメージ内部のポート番号8080固定 -it 作成したイメージ名


## REST形式でのアクセス
起動後にブラウザでアクセスするとJSON形式で返却する
（初回の起動は時間がかかります）

```
http://localhost/deitail?kanji=山田　太郎&kana=ヤマダ　タロウ
```

詳細は「別紙　基本設計書（漢字カナ突合モデル）」を参照


# 履歴

## v1.2 20241028
- 長い入力文字列に対して，応答時間がかかっていたのを修正
- 詳細モデルで，モデル単体で1.1に比較して，1/2-1/3の応答時間へと修正。
  簡易モデルは応答時間変更なし。
  

## v1.1 20240917
DJLライブラリをあらかじめダウンロードしたものを追加。 
.djl.ai
.cache
ディレクトリから，Dockerへコピー

## v1.0 20240828
初期リリース