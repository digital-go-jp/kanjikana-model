# AIモデル訓練用スクリプト

datasetのデータを用いて，漢字姓名（外国人の場合にはアルファベット姓名）とカナ姓名をtransformerで学習し，漢字姓名からカナ姓名，もしくは，カナ姓名から漢字姓名を推測する。

漢字姓名からカナ姓名を推計するモデルの実行ほう方法は[training.ipynb](./training.ipynb)を，カナ姓名から漢字姓名を推計するモデルの実行方法は[training_r.ipynb](./training_r.ipynb)を参照のこと。


各スクリプトの詳細については下記の通りである。


## 事前準備

- [mergedic.py](./mergedic.py)    
  json形式の２つの辞書ファイルをマージする。（同じ漢字とカナのペアがあったものはマージする）    
  
  ```bash
  python mergedic.py --dic1 oss.json --dic2 seimei.json --outfile tmp.json
  ```

  - \--dic1    
    マージする一つ目のJSONファイル
  - \--dic2    
   マージする二つ目のJSONファイル   
  - \--outfile     
  マージされたJSONファイル


- [prep.py](./prep.py)    
  引数のjsonで指定されるデータを訓練用(train.src, train.tgt)，開発用(valid.src, valid.tgt)，検証用(test.src, test.tgt)に分ける。開発用と検証用がそれぞれ全体の引数で指定されるratioの率になる。
  - train.src    
    訓練用の入力側のデータ。漢字姓名からカナ姓名を推計するモデルのときには漢字姓名，カナ姓名から漢字姓名を推計するモデルのときはカナ姓名
  - train.tgt    
    訓練用の出力側のデータ。漢字姓名からカナ姓名を推計するモデルのときにはカナ姓名，カナ姓名から漢字姓名を推計するモデルのときは漢字姓名
  

  ```bash
  python prep.py --json tmp.json --outdir dataset --ratio 0.01
  ```
  - \--json    
    入力のJSONファイル
  - \--outdir    
    出力されるファイルの格納ディレクトリ
  - \--ratio    
    開発用とテスト用を全体のどれくらいの割合にするか。0.01を指定すると，訓練用が98%，開発用が1%，検証用が1%となる

- [catawk.py](./catawk.py)       
  入力されたCSVデータの列位置を指定して，ファイルの行の上位からのスタート位置％とエンド位置％の間のデータを，指定されたファイルに追加する

  ```bash
  python catawk.py --begin_idx_per 0 --end_idx_per 1 --appendfile valid.src --index 0 --infile wikiname.txt --infile_delimiter tsv
  ```

  - \--infile    
    追加するファイル
  - \--begin_idx_per    
   ファイルから抜き出す先頭の位置，ファイル全体行からのパーセント    
  - \--end_idx_per    
   ファイルから抜き出す末尾の位置，ファイル全体行からのパーセント    
  - \--appendfile    
   追加されるファイル      
  - \--index    
   漢字，カナで構成されるCSVファイルの何列目を抜き出すか？０オリジン    
  - \--infile_delimiter     
  infileファイルの区切り文字,csvかtsvを指定する。

- [kana.py](./kana.py)    
  ひらがなとカタカナの一文字ずつのペアを作成する

  ```bash
  python kana.py --outfile hirakata.txt --reverse
  ```

  - \--outfile    
  出力されるファイル名
  - \--reverse    
   このオプションを指定した時、カタカナから漢字を学習するデータセットを作成する


- [jsontotext.py](./jsontotext.py)    
  jsonで作成された辞書データを，CSV形式へ変換する

  ```bash
  python jsontotext.py --jsonfile tankanji.json --outfile tankanji.txt --reverse
  ```

  - \--jsonfile    
    入力するJSONファイル
  - \--outfile     
    出力するCSVファイル 
  - \--reverse   
   このオプションを指定した時、カタカナから漢字を学習するデータセットを作成する

- [omit_testval.py](./omit_testval.py)    
  開発，検証用に，訓練データに入っているものがあれば，削除する(インサンプルを阻止)

  ```bash
  python omit_testval.py --train_src train.src --train_tgt train.tgt --test_src test.src --test_tgt test.tgt --valid_src valid.src --valid_tgt valid.tgt
  ```

  - \--train_src    
    訓練用の入力側のデータ
  - \--train_tgt    
    訓練用の出力側のデータ
  - \--valid_src    
    開発用の入力側のデータ。上書きされる
  - \--valid_tgt    
    開発用の出力側のデータ。上書きされる
  - \--test_src    
    検証用の入力側のデータ。上書きされる
  - \--test_tgt    
    検証用の出力側のデータ。上書きされる

- [shuffle.py](./shuffle.py)    
  漢字・アルファベットとカタカナの２つのファイルを入力しそれぞれの位置がペアとなるようにしたままで，順番をシャッフルする

  ```bash
  python shuffle.py --src train.src --tgt train.tgt
  ```
  - \--src    
    入力側のデータファイル。１列のデータ。tgtと行が対応する。
  - \--tgt    
    出力側のデータファイル，１列のデータ。srcと行が対応する。


- [space.py](./space.py)    
  ファイル内のデータを，文字単位でスペース区切りにする

  ```bash
  python space.py --infile train.src --outfile train.src
  ```
  - \--infile    
    入力ファイル

  - \--outfile    
    出力ファイル


- [format.py](./format.py)     
  ２つの漢字・アルファベットとカタカナの入ったファイルから，JSONL形式に変換する

  ```bash
  python format.py --src train.src --tgt train.tgt --outfile train.jsonl --src_key kanji --tgt_key kana
  ```

  - \--src    
    入力側のデータファイル。１列のデータ。tgtと行が対応する。
  - \--tgt    
    出力側のデータファイル，１列のデータ。srcと行が対応する。
  - \--tgt_key    
    jsonlファイルのtgtの辞書のキー
  - \--src_key    
    jsonlファイルのsrcの辞書のキー
  - \--key    
    jsonlファイルのsrcとtgtで作成される辞書型のキー
  - \--outfile    
    出力されるJSONLファイル


## 学習用スクリプト

- [trainsfer_model.py](./transformer_model.py)        
   Transformerモデルで，訓練データと開発データを用いて学習を行う。

   ```bash
   python ./transformer_model.py \
  --emb_size 512 \
  --nhead 8 \
  --ffn_hid_dim 2048 \
  --batch_size 64 \
  --num_encoder_layers 8 \
  --num_decoder_layers 8 \
  --lr 0.00002 \
  --dropout 0.3 \
  --num_epochs 50 \
  --device cuda \
  --earlystop_patient 3 \
  --output_dir model \
  --tensorboard_logdir logs \
  --prefix translation \
  --source_lang kanji \
  --target_lang kana \
  --train_file train.jsonl \
  --valid_file valid.jsonl
   ```

  - \--emb_size     
    入力，出力の文字のエンベッドのDimension

  - \--nhead        
    マルチヘッド数

  - \--ffn_hid_dim    
    FFNのDimension

  - \--num_encoder_layer    
    エンコードレイヤーの数

  - \--num_decoder_layer    
    デコードレイヤーの数

  - \--lr     
    学習率

  - \--dropout    
    ドロップアウトの割合, 0-1

  - \--num_epochs    
    何周学習データを用いて学習を行うか

  - \--device    
    mps,cpu,cudaから選択
    - mps        
     アップルシリコンを搭載したマシンで実行する際に選択
    - cuda         
     CUDAが利用できる環境で選択
    - cpu        
     上記以外は，CPUモードを選択

  - \--earlystop_patient    
    開発用データでlossが下がらなくなってearlystop_patient回計算が終了した場合に，num_epochs数以下でも，計算を終了させる

  - \--output_dir    
   モデルの出力ディレクトリ。モデルは，二つ出力される
     - checkpoint_xxx.pt
       xxxの部分に，このモデルで実行した直近のepoch数が入る。
     - checkpoint_best.pt    
       直近のepoch数までの計算において，最も開発用データにおけるLOSSが少なかったモデル

  - \--tensorboard_logdir    
    tensorboard形式のログの出力ディレクトリ。学習状況を確認するためには`tensorboard --logdir logs`を実行後，ブラウザでhttp://localhost:6000/から確認
 

  - \--prefix    
    jsonl形式のデータのprefix

  - \--source_lang    
    jsonl形式のデータのsourceのキー

  - \--target_lang    
    jsonl形式のデータのtargetのキー

  - \--train_file    
   訓練用データのJSONLファイル

  - \--valid_file    
   開発用データのJSONLファイル


## 検証用スクリプト

- [generate_batch.py](./generate_batch.py)    
  検証用データを用いて，モデルで推論し，推論した結果と，元の出力側のデータと比較し，正解率を出力する

  ```bash
  python generate_batch.py \
  --test_file test.jsonl \
  --model_file checkpoint_best.pt \
  --outfile generate.txt \
  --device cpu \
  --nbest 5 \
  --beam_width 5 \
  --max_len 100 \
  --search beam
  ```

  - \--test_file    
    検証用データ，JSONLファイル
  - \--model_file    
    学習用スクリプトで出力されたモデルファイル
  - \--outfile    
    モデルによって推論された結果が出力されるファイル
  - \--device    
    mps,cpu,cudaから選択。検証の時はCPUでよい。
    - mps        
     アップルシリコンを搭載したマシンで実行する際に選択
    - cuda         
     CUDAが利用できる環境で選択
    - cpu        
     上記以外は，CPUモードを選択
  - \--nbest    
    searchでbeamを指定した際に，確率の高い方からいくつ出力するか

  - \--beam_width    
     searchでbeamを選択した際に，ビーム幅を幾つにするか？ nbest以上の値をセットする
  
  - \--max_len    
    出力する推論の文字列を最大何文字で打ち切るか

  - \--search    
    推論の際の検索のタイプ。 
    - beam    
      ビームサーチ
    - greedy    
      貪欲法によるサーチ


## モデル変換用

- [convert_jitscript.py](./convert_jitscript.py)        
  JavaのDjLライブラリで読み込めるように，TorchScriptへ変換する。

  ```bash
  python convert_jitscript.py  \
    --model_file=checkpoint_best.pt \
    --model_script=script.pt \
    --encoder=encoder.pt \
    --decoder=decoder.pt \
    --positional_encoding=positional_encoding.pt \
    --generator=generator.pt \
    --src_tok_emb=src_tok_emb.pt \
    --tgt_tok_emb=tgt_tok_emb.pt \
    --vocab_src=vocab_src.txt \
    --vocab_tgt=vocab_tgt.txt \
    --params=params.json \
    --device=cpu

  ```

  - \--device    
    mps,cpu,cudaから選択。検証の時はCPUでよい。
    - mps        
     アップルシリコンを搭載したマシンで実行する際に選択
    - cuda         
     CUDAが利用できる環境で選択
    - cpu        
     上記以外は，CPUモードを選択