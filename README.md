# Name Kanji/Kana Matching Model

English | [日本語](./README_ja.md)

For details about this program and how to use it, see the [Name Matching Support Service website](https://kktg.digital.go.jp/support/index.html).

## Overview

The Name Kanji/Kana Matching Model matches a name written in kanji or the Latin alphabet with its reading written in katakana.
The model provides the following three features:

- Name kanji/kana matching

  Takes a name written in kanji or the Latin alphabet and a name written in katakana, and determines whether they match. The result is a score from 0 to 100; a higher score indicates a higher degree of match.

- Name katakana prediction

  Given a name written in kanji or the Latin alphabet, outputs predicted katakana readings in descending order of estimated probability.

- Name kanji prediction

  Given a name written in katakana, outputs corresponding names in kanji or the Latin alphabet in descending order of estimated probability.

## Data Used

The model uses pairs of kanji or Latin-alphabet names and their readings in katakana.
The following data sources were used. Please check the license of each dataset before using it.

### Dictionary data

- [Canna](https://canna-input.github.io/)
- [FreeWnn](http://www.tomo.gr.jp/FreeWnn/)
- [IPAdic](https://taku910.github.io/mecab/)
- [KAKASI](http://kakasi.namazu.org/)
- [Mozc](https://github.com/google/mozc/)
- [NEologd](https://github.com/neologd/mecab-ipadic-neologd/)
- [SKK](https://github.com/skk-dev/dict/)

### Variant character data

- [MJ](https://moji.or.jp/mojikiban/) The MJ Character Information List is provided under the [Creative Commons Attribution-ShareAlike 2.1 Japan License](https://creativecommons.org/licenses/by-sa/2.1/jp/).

### Web data

- [Wikipedia](https://dumps.wikimedia.org/)

## Models

For details about the algorithms used by each feature, see “Name Kanji/Kana Matching Model” on the [Name Matching Support Service website](https://kktg.digital.go.jp/support/index.html).

### Dictionary model

This model uses a dictionary of kanji or Latin-alphabet names and their readings in katakana to determine whether an input name matches.
The dictionary can be created by downloading the data listed above under “Data Used” and running the provided scripts.

For details, see the [dict](./dict/README.md) documentation.

The dictionary files used by the resulting dictionary model are distributed under “Resources” on the [Name Matching Support Service website](https://kktg.digital.go.jp/support/index.html).

### Foreign-name model

This model takes a name written in the Latin alphabet and a name written in katakana, and matches them based on the “Correspondence Table for Japanese (Romanized) and English Name Components.”
For details, see the following publication:

[Satoshi Sato. Translation of the List of Participants in the 2020 Tokyo Olympics. Journal of Natural Language Processing, Vol. 30, No. 2, pp. 748–772, 2023.](https://www.jstage.jst.go.jp/article/jnlp/30/2/30_748/_article/-char/ja)

### AI model

Datasets are created from pairs of kanji or Latin-alphabet names and katakana names extracted from the data listed above under “Data Used.” A Transformer model is then trained on these datasets.

For the name kanji/kana matching feature, a binary classification model is also trained using pairs of kanji or Latin-alphabet names and katakana names. Each pair is labeled as a match or a non-match, and the model predicts which class the pair belongs to.

During training, kanji, Latin-alphabet, and katakana strings are split into individual characters and supplied to the Transformer as input and output.
For details, see the [train](./train/README.md) documentation.

Four types of models are provided:

- A model that predicts katakana from kanji or Latin-alphabet input

  Used by the name kanji/kana matching and name katakana prediction features.
  For details about training, see [training.ipynb](./train/training.ipynb).
  For details about fine-tuning a trained model, see [finetuning.ipynb](./train/finetuning.ipynb).

- A model that predicts kanji or Latin-alphabet names from katakana

  Used by the name kanji prediction feature.

  For details about training, see [training_r.ipynb](./train/training_r.ipynb).

- A binary classification model that determines whether a kanji name and a katakana name match

  Used by the name kanji/kana matching feature. For details about creating the training data, see [dataset_j.ipynb](./train/dataset_j.ipynb); for training, see [training_j.ipynb](./train/training_j.ipynb).

- A binary classification model that determines whether a Latin-alphabet name and a katakana name match

  Used by the name kanji/kana matching feature. For details about creating the training data, see [dataset_e.ipynb](./train/dataset_e.ipynb); for training, see [training_e.ipynb](./train/training_e.ipynb).

Pretrained models are distributed under “Resources” on the [Name Matching Support Service website](https://kktg.digital.go.jp/support/index.html).

## Running the Program

This model is provided as a program that can be run from the command line.
It is also used internally by the [Name Matching Support Service](https://api.kktg.digital.go.jp/).

For program details, see the [core](./core/README.md) documentation.
For Web API setup and usage, see the [api](./api/README.md) documentation.

## MCP Server

A Spring AI-based MCP server is available over stdio. It invokes the kanjikana core Java source directly without calling the REST API. See the [MCP server README](./mcp-server/README.md) for architecture diagrams, tool flows, and build/run instructions.

## Sample Programs

The AI model is built with Python and PyTorch. Sample prediction programs are provided in Python and Java.

For details, see the [sample](./sample/README.md) documentation.

## References

The foreign-name model included in this project (for matching names in the Latin alphabet with names in katakana) is a Java port of a Ruby program written by Professor Satoshi Sato of Nagoya University.

- Satoshi Sato. Translation of the List of Participants in the 2020 Tokyo Olympics. Journal of Natural Language Processing, Vol. 30, No. 2, pp. 748–772, 2023.
  [Paper](https://www.jstage.jst.go.jp/article/jnlp/30/2/30_748/_article/-char/ja)

## History
For release history, see [CHANGES.md](./CHANGES.md).
