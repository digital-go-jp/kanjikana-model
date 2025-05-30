#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


"""
Cannaから辞書ファイルを抽出し，漢字・アルファベットとカタカナのペアを作成する。通常の辞書をパースしているため，氏名以外も含まれる

"""

import argparse
import os
import json
import jaconv
import glob
import regex

kanji_pattern=regex.compile(r'[\p{Script=Latin}\p{Script=Han}\p{Script=Katakana}\p{Script=Hiragana}]+')
kana_pattern=regex.compile(r'\p{Script=Katakana}+')
def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    lst = []
    for fname in glob.glob(args.indir+"/*.p"):
        with open(fname,'r',encoding='euc-jp') as f:
            for l in f:
                items = l.rstrip().split(" ")
                kanji=items[1]
                kana=jaconv.hira2kata(items[0])   # ひらがなをカタカナに変換
                if not kanji_pattern.fullmatch(kanji):  # 正規表現で漢字・アルファベットの条件にマッチしているか
                    continue
                if not kana_pattern.fullmatch(kana):  # 正規表現でカタカナの条件にマッチしているか
                    continue
                lst.append(f'{kanji},{kana}')

    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in lst:
            f.write(l+"\n")
def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="Canna37p3/dic/ideo/pubdic", type=str)
    parser.add_argument('--outfile', default="./dict.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
