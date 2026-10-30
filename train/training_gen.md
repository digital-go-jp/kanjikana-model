# `transformer_model.py` のアルゴリズム解説

この文書では [`transformer_model.py`](./transformer_model.py) のデータ読み込み、トークン化、Encoder–Decoder Transformer、学習・検証、チェックポイント処理をコードの実装に沿って説明する。

## 1. 何を学習するプログラムか

このプログラムは、入力側 (`source_lang`, 既定値 `kanji`) の文字列から、出力側 (`target_lang`, 既定値 `kana`) の文字列を生成する系列変換モデルを学習する。入力の各文字をトークンとして読み込み、出力を先頭から1トークンずつ予測する。

教師データはJSON Lines形式であり、各行から `args.prefix`（既定値 `translation`）の下の `source_lang` と `target_lang` の値を取り出す。既定の組み合わせでは概ね次のような構造を想定する。

```json
{"translation": {"kanji": "山田", "kana": "ヤマダ"}}
```

ソースコードではモデルが扱う文字列を空白区切りの文字トークン列に変換し、学習時には正しい出力文字列を教師信号として使う。二値分類ではなく、出力系列全体を生成する文字単位の翻訳・変換タスクである。

## 2. データの読み込みと文字単位のトークン化

### データセット

`KanjiKanaDataSet` は入力ファイルを1行ずつJSONとして読み、指定されたprefix・source・targetの値をペアで保持する。`__getitem__` では各文字を1要素ずつのリストに分け、空白で連結して返す。

例えば `"山田"` は `"山 田"`、`"ヤマダ"` は `"ヤ マ ダ"` の形になる。これにより、単語単位ではなく1文字単位で系列を扱う。データセットには「文字を分割してスペースで戻す」という変換が含まれている。

### WordLevelトークナイザー

`get_tokenizer_by_corpus` は学習データを走査し、空白を区切りとして使うWordLevelトークナイザーを作る。語彙は入力側と出力側で別々である。したがって、同じ文字であってもsourceとtargetでIDが同じとは限らない。

予約トークンはID順に次のように置かれる。

| ID | トークン | 用途 |
|---:|---|---|
| 0 | `<unk>` | 未知トークン |
| 1 | `<pad>` | バッチ内系列長を揃える埋め草 |
| 2 | `<bos>` | 系列の開始 |
| 3 | `<eos>` | 系列の終了 |

本文中の文字列は、トークナイズ → 語彙ID化 → `<bos>` と `<eos>` の追加、の順に変換される。実装上、`get_tokenizer_by_vocab` は学習済み語彙からトークナイザーを組み立て、`get_tokenizer_by_corpus` は学習コーパスから語彙を新規作成する。

## 3. バッチの形とマスク

### Padding

異なる文字数の系列を同じバッチにまとめるため、`collate_fn` は `pad_sequence` で不足分を `PAD_IDX` (= 1) で埋める。データのテンソル形状は `(系列長, バッチサイズ)` である。PyTorch Transformerの既定の系列優先形式に合わせている。

sourceとtargetは別々にpaddingする。入力側・出力側それぞれにpaddingマスクを作り、Transformerが `<pad>` の位置を有効な文字として参照しないようにする。

### Decoderの因果マスク

教師系列の未来文字をDecoderが先読みしないよう、`generate_square_subsequent_mask` が三角マスクを作る。位置 \(i\) が位置 \(j\) を参照できるのは \(j \le i\) のときだけである。未来位置には \(-\infty\) を設定し、Attentionのsoftmax後に重みが0になるようにする。

このマスクにより、学習時でも出力位置 \(i\) の予測は、出力の正解系列におけるそれ以前の文字だけを使う自己回帰的な条件となる。

### Sourceマスク

`create_mask` はsource用の全Falseマスクも作る。これはsource系列に対する位置方向の因果制約がないことを表し、Encoderは入力の前後の文字を参照できる。paddingの除外は別の `src_padding_mask` で行う。

## 4. Transformerモデルの計算

`Seq2SeqTransformer` はPyTorchの `nn.Transformer` に、source/target用のEmbedding、位置エンコーディング、出力語彙への線形変換を組み合わせた構成である。

### 4.1 トークンEmbeddingとスケーリング

sourceとtargetで別々のEmbedding行列を使い、トークンID \(x_i\) を \(d\) 次元ベクトルに変換する。さらに埋め込み全体を次の係数で拡大する。

\[
e_i = \mathrm{Embedding}(x_i)\sqrt{d}
\]

これはAttentionに入力する埋め込みのスケールを調整する処理である。ここで \(d\) は `emb_size` である。

### 4.2 正弦・余弦位置エンコーディング

Attentionだけでは系列の順序が分からないため、各位置 \(pos\) に固定の位置ベクトルを加算する。埋め込み次元 \(2i\) と \(2i+1\) には次の値を使う。

\[
PE(pos, 2i) = \sin\left(pos \cdot 10000^{-2i/d}\right)
\]

\[
PE(pos, 2i+1) = \cos\left(pos \cdot 10000^{-2i/d}\right)
\]

したがってTransformerに入るベクトルは `token_embedding + pos_embedding` である。その後にDropoutを適用する。位置ベクトルは学習パラメーターではなくbufferとして登録され、既定の最大長は5000である。

### 4.3 Encoder

Encoderはsourceの各文字について、入力系列内の他の文字との関係を自己Attentionで計算する。各層では複数headのAttentionと位置ごとのFeed-Forwardネットワークが使われ、入力文脈を反映した隠れ表現を作る。source側に因果マスクがないため、各入力文字は前後両方の文字を参照できる。Padding位置はpadding maskで除外される。

Scaled dot-product attentionの基本形は次のとおりである。

\[
\mathrm{Attention}(Q,K,V)
= \mathrm{softmax}\left(\frac{QK^\top}{\sqrt{d_k}} + M\right)V
\]

ここで \(Q\) はquery、\(K\) はkey、\(V\) はvalue、\(d_k\) は1 headの次元、\(M\) はマスクである。複数headでは異なる射影空間でこの計算を並列に行い、その結果を結合する。

### 4.4 Decoder

Decoderは出力済みのtarget文字列から次の文字を予測する。各Decoder層は概ね次の順に計算する。

1. **Masked self-attention**: targetの過去位置だけを参照する。
2. **Encoder–Decoder attention**: Encoderのsource表現を参照し、入力中の関連位置に注目する。
3. **Feed-Forward**: 各位置の表現を非線形変換する。

target側には未来を遮る `tgt_mask` とpadding位置を隠す `tgt_padding_mask` を適用する。Encoder出力を参照するAttentionにはsourceのpaddingマスクを渡す。

### 4.5 語彙logit

TransformerのDecoder出力 \(h_t\) を線形層に通し、target語彙サイズ \(V\) のlogitに変換する。

\[
z_t = W h_t + b \quad (z_t \in \mathbb{R}^{V})
\]

このlogitのsoftmaxが、位置 \(t\) の次トークン候補それぞれの確率となる。学習ではlogitをCross Entropyに渡す。推論で確率にしたり文字列として出力したりする処理は、このファイルの学習ループではなく、別の生成処理が担当する。

## 5. Teacher forcingによる次トークン学習

各バッチのtarget系列には `<bos>` と `<eos>` が含まれる。学習ではtargetを1トークンずらして入力と正解に分ける。

- Decoder入力: `tgt[:-1, :]` — `<bos>` とそれまでの正解文字
- 正解出力: `tgt[1:, :]` — 次の正解文字と最後の `<eos>`

例えば正解系列が

```text
<bos> ヤ マ ダ <eos>
```

なら、Decoder入力と正解出力は位置ごとに次の対応となる。

| Decoder入力 | 予測すべき正解 |
|---|---|
| `<bos>` | `ヤ` |
| `<bos> ヤ` | `マ` |
| `<bos> ヤ マ` | `ダ` |
| `<bos> ヤ マ ダ` | `<eos>` |

このように正解の過去部分をDecoderに与えて、各位置で次の1文字を当てる学習をTeacher forcingと呼ぶ。因果マスクを併用するため、Decoderの各位置が正解の未来文字を直接見ることはない。

## 6. 損失関数とパラメーター更新

損失関数は `CrossEntropyLoss(ignore_index=PAD_IDX)` である。正解tokenを \(y_t\)、位置 \(t\) の語彙logitを \(z_t\) とすると、paddingでない位置の損失は

\[
\ell_t = -\log
\frac{\exp(z_{t,y_t})}{\sum_{v=1}^{V}\exp(z_{t,v})}
\]

である。`ignore_index=PAD_IDX` により、正解がpaddingの位置は損失に含めない。`PAD_IDX` は1で、`UNK_IDX` は0なので、未知文字は損失計算から除外されず、通常のトークンとして学習対象となる。

各バッチでの更新は次の順である。

1. 入力・targetを計算デバイスへ移動する。
2. targetを入力部分と1文字先の正解部分に分ける。
3. source/target maskを作る。
4. Transformerで全target位置の語彙logitを計算する。
5. `(系列長, バッチサイズ, 語彙サイズ)` のlogitと正解を一次元に並べ、Cross Entropyを計算する。
6. `optimizer.zero_grad()` で古い勾配を消し、`loss.backward()` で勾配を逆伝播する。
7. AdamでEmbedding、Attention、Feed-Forward、出力層などのパラメーターを更新する。

Adamは既定で \(\beta_1=0.9\)、\(\beta_2=0.98\)、`eps=adam_eps` である。勾配の一次・二次モーメントを使ってパラメーターごとに更新幅を調整する。重み減衰は指定されていない。

## 7. 1エポックの学習・検証

### 学習

`train_epoch` はモデルを学習モードにし、訓練データから `batch_size` 件ずつ、シャッフルしてバッチを作る。各バッチの平均Cross Entropyを加算し、最後にバッチ数で割ってエポック損失として返す。

この平均はサンプル数や非padding token数で重み付けした平均ではなく、**バッチごとの平均損失をバッチ数で割った値**である。したがって最後の小さいバッチも、通常サイズのバッチと同じ重みでエポック平均に寄与する。

### 検証

`evaluate` はモデルを評価モードにし、`valid_file` から検証データセットを新しく作って評価する。検証用DataLoaderも `shuffle=True` だが、損失の集計値はサンプルの順番には依存しない。検証損失も学習損失と同様にバッチ平均の単純平均である。

`evaluate` では `model.eval()` は呼ぶが、`torch.no_grad()` または `torch.inference_mode()` は使っていない。したがって検証中にも勾配計算グラフが構築される。検証損失を逆伝播したりoptimizer更新したりはしないが、不要な計算・メモリを消費する実装である。

## 8. モデル作成・初期化

`load_model` は指定されたEncoder/Decoder層数、埋め込み次元、head数、語彙サイズ、Feed-Forward次元、Dropoutでモデルを作る。その後、パラメーターの次元数が2より大きいものにXavier uniform初期化を適用する。1次元のbiasなどはこのループでは初期化し直さない。

コマンドラインの既定値は次のとおりである。

| 引数 | 既定値 |
|---|---:|
| `emb_size` | 512 |
| `nhead` | 8 |
| `ffn_hid_dim` | 2048 |
| `batch_size` | 64 |
| `num_encoder_layers` | 8 |
| `num_decoder_layers` | 8 |
| `num_epochs` | 50 |
| `lr` | 0.00002 |
| `dropout` | 0.3 |
| `adam_eps` | 0.000001 |
| `earlystop_patient` | 99999 |
| `device` | `mps` |

`nhead` は `emb_size` を割り切る必要がある。デバイスの既定値はMPSだが、利用環境でそのデバイスが使えることを自動判定する処理はこのスクリプトにはない。必要に応じて `--device cuda` または `--device cpu` を指定する。

## 9. エポック管理、早期終了、保存

`train` はエポックごとに訓練損失・検証損失を計算して表示し、`checkpoint_NNN.pt` を保存する。チェックポイントにはエポック、モデル状態、optimizer状態、訓練損失、source/target語彙、モデル設定が含まれる。

検証損失がこれまでの最良値を下回った場合は `checkpoint_best.pt` も保存し、`patient` を0に戻す。改善しなければ `patient` を1増やす。停止条件は

\[
\text{patient} > \text{earlystop\_patient}
\]

である。既定値が99999なので、既定設定では実質的に最大epoch数まで続ける。保存済みチェックポイントは、`save_num + 1` 個より古いものを削除する。ただし、この削除対象の並びには `checkpoint_best.pt` も `checkpoint_*.pt` のglobに一致して含まれるため、ファイル名の辞書順で保持・削除が決まる。

### 実装上の再開処理の注意点

再開時のファイル選択は `checkpoint_*.pt` を逆順ソートし、ファイルが2つ以上ある場合に `files[0]` をbest、`files[1]` を最後のチェックポイントとして扱う。`checkpoint_best.pt` が存在する場合は辞書順で番号付きファイルより先になるため、この2ファイルが意図どおり選ばれる。一方、bestファイルが存在しないまま番号付きチェックポイントが2つ以上ある場合は、`files[0]` と `files[1]` の両方が番号付きファイルになり、最新の番号付きファイルをbest側として扱い、その次に新しいファイルから重みを再開する。bestファイルの有無を個別に確認していないため、常にbestとlatestを適切に選べるわけではない。

さらに、保存される辞書には検証損失を表す `val_loss` キーがない（`loss` キーには訓練損失を保存している）。一方、再開時は `best_checkpoint` から `val_loss` を読もうとする。そのため、このスクリプトが作ったチェックポイントからはbest validation lossが復元されず、再開後の `best_loss` は `None` のままになる可能性がある。その場合、再開後の最初の検証損失が新しい最良値として扱われる。

チェックポイントを削除する処理と再開時の選択はファイル名の単純な辞書順に依存している。再開時にどの重み・optimizer状態が使われるかを厳密に管理するには、保存対象とロード対象を役割別に明示して確認する必要がある。

## 10. TensorBoardと実行時間表示

`tensorboard_logdir` が空でなければSummaryWriterを作り、各エポックの訓練損失と検証損失を記録する。空文字列ならTensorBoard writerを作らない。

エポック開始時にtimerを起動し、`train_epoch` が終わった直後に終了時刻を記録している。その後に `evaluate` を実行するため、表示される `Epoch time` は検証時間を含まず、訓練部分の経過時間である。

## 11. 主要な流れを擬似コードで表す

```text
訓練・検証用JSONLを読む
source文字列とtarget文字列を文字トークン列にする
source語彙・target語彙を作る
Encoder–Decoder Transformer、Cross Entropy、Adamを初期化

各epoch:
    訓練データをシャッフルしてバッチ化
    各バッチ:
        sourceとtargetをpadding
        target入力 = BOSを含む正解系列の先頭部分
        target正解 = 1文字右にずらした系列（EOSまで）
        causal maskで未来targetを隠す
        Transformerで各位置の次文字logitを計算
        PADを除外したCross Entropyを計算
        逆伝播してAdamで更新
    検証データで同じ次文字予測損失を計算
    epochチェックポイントを保存
    検証損失が改善したらbest checkpointを保存
    patience条件を超えたら終了
```

要するに、source側の漢字系列をEncoderで文脈表現にし、Decoderがそれと出力済みのカタカナ文字列を参照しながら次のカタカナ文字を予測する。正解文字列の各位置で次文字の確率を学習し、padding以外の位置のCross Entropyを下げるようTransformer全体をAdamで更新する処理である。

## 12. このファイルの範囲と補足

`Seq2SeqTransformer.forward` は、系列位置ごとの語彙logitを返す。また `encode` と `decode` はTorchScript export対象として定義されている。一方、このファイルの学習処理には、生成時にargmax・beam search・samplingのどれを使うかという出力デコード戦略は含まれていない。実際の推論時の逐次生成は、利用側の生成コードで確認する必要がある。

モデルには中間層出力を保存するhook用コードもあるが、登録・返却部分はコメントアウトされている。そのため、通常の `forward` が返すのは語彙logitのみである。
