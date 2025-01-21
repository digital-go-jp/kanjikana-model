リソース
######################


あらかじめ作成した辞書ファイル及び，計算済みのAIモデルは以下に格納している。



v1.6 (2025/03/01)
=================================


辞書
-------------
  - 姓名辞書

    - `1.6/dict/oss.json <1.6/dict/oss.json>`_ 
    - `1.6/dict/seimei.json <1.6/dict/seimei.json>`_ 
    - `1.6/dict/crawl.json <1.6/dict/crawl.json>`_ (空ファイル)
    - `1.6/dict/statistics.json <1.6/dict/statistics.json>`_ (空ファイル)

  - 異体字辞書

    - `1.6/dict/itaiji.json <1.6/dict/itaiji.json>`_ 

  - 単漢字辞書

    - `1.6/dict/tankanji.json <1.6/dict/tankanji.json>`_ 

姓名データ
-----------------
    - `1.6/wikipedia/wikiname.txt <1.6/wikipedia/wikiname.txt>`_ 


AIモデル(漢字・アルファベットからカタカナを推計)
-------------------------------------------------------------------------------------
- Pytorchモデル 

  - `1.6/ai/checkpoint_best.pt <1.6/ai/checkpoint_best.pt>`_ 

- Java DJL用 jit script
  
  - `1.6/ai/encoder.pt/encoder.pt <1.6/ai/encoder.pt>`_
  - `1.6/ai/encoder.pt/decoder.pt <1.6/ai/decoder.pt>`_
  - `1.6/ai/encoder.pt/generator.pt <1.6/ai/generator.pt>`_ 
  - `1.6/ai/encoder.pt/positional_encoding.pt <1.6/ai/positional_encoding.pt>`_ 
  - `1.6/ai/encoder.pt/script.pt <1.6/ai/script.pt>`_ 
  - `1.6/ai/encoder.pt/src_tok_emb.pt <1.6/ai/src_tok_emb.pt>`_ 
  - `1.6/ai/encoder.pt/tgt_tok_emb.pt <1.6/ai/tgt_tok_emb.pt>`_
  - `1.6/ai/encoder.pt/vocab_src.txt <1.6/ai/vocab_src.txt>`_ 
  - `1.6/ai/encoder.pt/vocab_tgt.txt <1.6/ai/vocab_tgt.txt>`_ 
  - `1.6/ai/encoder.pt/params.json <1.6/ai/params.json>`_ 

AIモデル(カタカナから漢字・アルファベットを推計)
-------------------------------------------------------------------------------------
- Pytorchモデル 

  - `1.6/ai_r/checkpoint_best.pt <1.6/ai_r/checkpoint_best.pt>`_ 

- Java DJL用 jit script
  
  - `1.6/ai_r/encoder.pt <1.6/ai_r/encoder.pt>`_
  - `1.6/ai_r/decoder.pt <1.6/ai_r/decoder.pt>`_
  - `1.6/ai_r/generator.pt <1.6/ai_r/generator.pt>`_ 
  - `1.6/ai_r/positional_encoding.pt <1.6/ai_r/positional_encoding.pt>`_ 
  - `1.6/ai_r/script.pt <1.6/ai_r/script.pt>`_ 
  - `1.6/ai_r/src_tok_emb.pt <1.6/ai_r/src_tok_emb.pt>`_ 
  - `1.6/ai_r/tgt_tok_emb.pt <1.6/ai_r/tgt_tok_emb.pt>`_
  - `1.6/ai_r/vocab_src.txt <1.6/ai_r/vocab_src.txt>`_ 
  - `1.6/ai_r/vocab_tgt.txt <1.6/ai_r/vocab_tgt.txt>`_ 
  - `1.6/ai_r/params.json <1.6/ai_r/params.json>`_ 

