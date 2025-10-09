# 辞書

オープンソースの辞書データとWikipediaからデータをダウンロードし，漢字・アルファベットとカタカナのペアの辞書を作成する。

辞書の作成方法については，[dictionary.ipynb](./dictionary.ipynb)を参照のこと。

各ディレクトリのスクリプトについては次のとおり。

## 共通

- [tankan.py](./tankan.py)   
  漢字・アルファベットとカタカナのペアのファイルから，漢字・アルファベット部分が一文字のもののみ抜き出して，単漢字辞書として作成する。

- [merge.py](./merge.py)    
   漢字・アルファベットとカタカナのペアファイルから，漢字・アルファベットをキーとし，その読みであるカタカナを複数持たせたJSON形式に成形する。また，漢字・アルファベットとカタカナのペアに，その由来をつけて，リストとして保持する。
   ```json
   {
       "太郎":{
          "タロウ":{"dics":["skk","kakasi"]},   // dicsをキーとして，由来をリストで保持する。
          "フトロウ":{"dics":["kakasi]},

   ```

## canna

  漢字カナ変換辞書データから，漢字・アルファベットとその読み仮名をカタカナに変換し，ペアを作成する。

- [extract_canna.py](canna/extract_canna.py)     
  Cannaから辞書ファイルを抽出し，漢字・アルファベットとカタカナのペアを作成する。通常の辞書をパースしているため，氏名以外も含まれる

- [words.py](canna/words.py)    
  Cannaから，地名などの名詞を抽出し，漢字・アルファベットとカタカナのペアを抜き出す。

## freewnn

  漢字カナ変換辞書データから，漢字・アルファベットとその読み仮名をカタカナに変換し，ペアを作成する。

- [extract_freewnn.py](freewnn/extract_freewnn.py)    
  Freewnnから辞書ファイルを抽出し，漢字・アルファベットとカタカナのペアを作成する。通常の辞書をパースしているため，氏名以外も含まれる

## ipadic

  漢字カナ変換辞書データから，漢字・アルファベットとその読み仮名をカタカナに変換し，ペアを作成する。

- [extract_ipadic.py](ipadic/extract_ipadic.py)    
   ipadicから，漢字・アルファベットとその読みを抜き出し，漢字・アルファベットとカタカナのペアを作成する。氏名以外のものも含まれる。

## kakasi
  
  漢字カナ変換辞書から，漢字・アルファベットとカタカナのペアを，異体字辞書から異体字のペアを抜き出す。

- [dict_kakasi.py](kakasi/dict_kakasi.py)    
   kakasiの漢字かな変換辞書から，，漢字・アルファベットとカタカナのペアを作成する。氏名以外のものも含まれる。

- [itaiji_kakasi.py](kakasi/itaiji_kakasi.py)    
  kakasiの異体字辞書から，異体字のペアを抜き出す。

## mj

  MJ文字情報一覧表は[クリエイティブ・コモンズ 表示 – 継承 2.1 日本 ライセンス条件](https://creativecommons.org/licenses/by-sa/2.1/jp/)で提供されています。
  
  Excelで提供されている，異体字リストから，異体字のペアを抜き出す。

- [extract_xlsx.py](mj/extract_xlsx.py)    
  mjのExcelファイルから単漢字辞書を作成する。データはExcel形式で提供されているので，Excel形式をCSV形式へ変換する[xlsx2csv](https://github.com/dilshod/xlsx2csv)を利用し，CSVに変換後にこのプログラムで異体字のペアを抽出する。


## mozc

  mozcからは，単語辞書と単漢字辞書，異体字辞書を作成する。単漢字辞書は単語辞書から一文字の漢字を抜き出したもの。

- [extract_mozc.py](mozc/extract_mozc.py)      
  mozcの漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。

- [extract_oss.py](mozc/extract_oss.py)    
  mozcの漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。
  
- [extract_single.py](mozc/extract_single.py)    
  mozcの単漢字と読み仮名から，単漢字辞書を抽出する。

- [extract_variant.py](mozc/extract_variant.py)    
  mozcの異体字辞書から，異体字のペアを作成する。

## neologd

  漢字カナ変換辞書データから，漢字・アルファベットとその読み仮名をカタカナに変換し，ペアを作成する。

- [extract_neologd.py](neologd/extract_neologd.py)    
  neologdの漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。

## skk

  漢字カナ変換辞書データから，漢字・アルファベットとその読み仮名をカタカナに変換し，ペアを作成する。また，異体字辞書から異体字を抜き出しペアを作成する。

- [fullname.py](skk/fullname.py)    
  skkの姓名辞書の漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。

- [jinmei.py](skk/jinmei.py)    
  skkの姓名辞書の漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。

- [itaiji_skk.py](skk/itaiji_skk.py)    
  skkの異体字辞書から，異体字のペアを作成する。

## wikipedia
 
  wikipediaからは，概要部分を抜き出して，文頭に　「漢字（読み）・・」　と記載されている場合に，漢字と読み仮名のペアを抜き出す。多少間違えている可能性があるので注意が必要。

- [extract_wiki.py](wikipedia/extract_wiki.py)    
  Wikipediaのダンプファイルから，概要部分を抜き出す。

- [extract_name.py](wikipedia/extract_name.py)    
  Wikipediaの概要部分から，名前のペアらしきものを抜き出す。
  概要の先頭の漢字，アルファベットを名前と認識し，その直後のカッコ内のカタカナを読みとして抽出する。
  ただし，この抽出基準に則っていないものもあるため，漢字・アルファベットとカタカナのペアに間違いが含まれる可能性がある。

- [select_name.py](wikipedia/select_name.py)    
  extract_name.pyによってWikipediaのダンプファイルから，概要部分が抜き出されたものに対して，漢字・アルファベットとカタカナのペアのうち，正しいと推測されるものを選択する。

  漢字・アルファベット部分に，漢字もしくはアルファベットだけのもののみ残す。
  漢字・アルファベットのスペースで区切られた単語数と，カタカナ部分のスペースで区切られた単語数が同じものを残す

- [dict_name.py](wikipedia/dict_name.py)    
  select_name.pyによってWikipediaのダンプファイルから作成した漢字・アルファベットとカタカナのペアを単語単位に分割する


- [check_dict.py](wikipedia/check_dict.py)    
  ossで指定したオープンソースの辞書のデータに，infileのデータのうち入っていない漢字とカナのペアを抜き出して，outfileに出力する

- [check_tankanji.py](wikipedia/check_tankanji.py)    
  tankanjiで指定した辞書のデータに，infileのデータのうち入っていない漢字とカナのペアを抜き出して，outfileに出力する


