リソース
######################


あらかじめ作成した辞書ファイル及び，計算済みのAIモデルは以下に格納している。



v1.6o (2025/03/01)
=================================


辞書
-------------
  - 姓名辞書

    - `1.6o/dict/oss.json <1.6o/dict/oss.json>`_ 
    - `1.6o/dict/seimei.json <1.6o/dict/seimei.json>`_ 
    - `1.6o/dict/crawl.json <1.6o/dict/crawl.json>`_ (空ファイル)
    - `1.6o/dict/statistics.json <1.6o/dict/statistics.json>`_ (空ファイル)

  - 異体字辞書

    - `1.6o/dict/itaiji.json <1.6o/dict/itaiji.json>`_ 

  - 単漢字辞書

    - `1.6o/dict/tankanji.json <1.6o/dict/tankanji.json>`_ 

姓名データ
-----------------
    - `1.6o/wikipedia/wikiname.txt <1.6o/wikipedia/wikiname.txt>`_ 


AIモデル(漢字・アルファベットからカタカナを推計)
-------------------------------------------------------------------------------------
- Pytorchモデル 

  - `1.6o/ai/checkpoint_best.pt <1.6o/ai/checkpoint_best.pt>`_ 

- Java DJL用 jit script
  
  - `1.6o/ai/encoder.pt/encoder.pt <1.6o/ai/encoder.pt>`_
  - `1.6o/ai/encoder.pt/decoder.pt <1.6o/ai/decoder.pt>`_
  - `1.6o/ai/encoder.pt/generator.pt <1.6o/ai/generator.pt>`_ 
  - `1.6o/ai/encoder.pt/positional_encoding.pt <1.6o/ai/positional_encoding.pt>`_ 
  - `1.6o/ai/encoder.pt/script.pt <1.6o/ai/script.pt>`_ 
  - `1.6o/ai/encoder.pt/src_tok_emb.pt <1.6o/ai/src_tok_emb.pt>`_ 
  - `1.6o/ai/encoder.pt/tgt_tok_emb.pt <1.6o/ai/tgt_tok_emb.pt>`_
  - `1.6o/ai/encoder.pt/vocab_src.txt <1.6o/ai/vocab_src.txt>`_ 
  - `1.6o/ai/encoder.pt/vocab_tgt.txt <1.6o/ai/vocab_tgt.txt>`_ 
  - `1.6o/ai/encoder.pt/params.json <1.6o/ai/params.json>`_ 

AIモデル(カタカナから漢字・アルファベットを推計)
-------------------------------------------------------------------------------------
- Pytorchモデル 

  - `1.6o/ai_r/checkpoint_best.pt <1.6o/ai_r/checkpoint_best.pt>`_ 

- Java DJL用 jit script
  
  - `1.6o/ai_r/encoder.pt <1.6o/ai_r/encoder.pt>`_
  - `1.6o/ai_r/decoder.pt <1.6o/ai_r/decoder.pt>`_
  - `1.6o/ai_r/generator.pt <1.6o/ai_r/generator.pt>`_ 
  - `1.6o/ai_r/positional_encoding.pt <1.6o/ai_r/positional_encoding.pt>`_ 
  - `1.6o/ai_r/script.pt <1.6o/ai_r/script.pt>`_ 
  - `1.6o/ai_r/src_tok_emb.pt <1.6o/ai_r/src_tok_emb.pt>`_ 
  - `1.6o/ai_r/tgt_tok_emb.pt <1.6o/ai_r/tgt_tok_emb.pt>`_
  - `1.6o/ai_r/vocab_src.txt <1.6o/ai_r/vocab_src.txt>`_ 
  - `1.6o/ai_r/vocab_tgt.txt <1.6o/ai_r/vocab_tgt.txt>`_ 
  - `1.6o/ai_r/params.json <1.6o/ai_r/params.json>`_ 

