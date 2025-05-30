#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
extract_name.pyによってWikipediaのダンプファイルから，概要部分が抜き出されたものに対して，漢字・アルファベットとカタカナのペアのうち，正しいと推測されるものを選択する。

漢字・アルファベット部分に，漢字もしくはアルファベットだけのもののみ残す。
漢字・アルファベットのスペースで区切られた単語数と，カタカナ部分のスペースで区切られた単語数が同じものを残す
"""


# 正しい組み合わせだけを抜き出す
import argparse
import json
import os
import re
import jaconv
import regex
import mojimoji

JPN=regex.compile(r'[\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Han}　 ・＝]+')
KATA=regex.compile(r'[\p{Script=Katakana}　 ・＝ー]+')
HIRA=regex.compile(r'[\p{Script=Hiragana}　 ・＝ー]+')
HIRAKATA=regex.compile(r'[\p{Script=Hiragana}\p{Script=Katakana}　 ・＝ー]+')
def run(args):
    if os.path.exists(args.outfile):
        os.remove(args.outfile)
    if os.path.exists(args.ngfile):
        os.remove(args.ngfile)
    def write(l):
        with open(args.outfile,'a',encoding='utf-8') as f:
            f.write(f'{l}\n')

    def writeng(l):
        with open(args.ngfile,'a',encoding='utf-8') as f:
            f.write(f'{l}\n')

    def is_katakana(s):
        return KATA.fullmatch(s) is not None

    def is_hiragana(s):
        return HIRA.fullmatch(s) is not None

    def is_yomigana(s):
        return HIRAKATA.fullmatch(s) is not None

    def is_alpha(s):
        return re.match(r'^[a-zA-Zａ-ｚＡ-Ｚ 　・＝]+$',s) is not None

    def is_midashi(s):
        if is_alpha(s) or JPN.fullmatch(s):
            return True
        return False

    lstok=[]
    lstng=[]
    with open(args.infile,'r',encoding='utf-8') as f:
        for i, l in enumerate(f):
            l = l.rstrip()
            if i % 10000 == 0:
                print(f'i={i}')
            items = l.rstrip().split('\t')
            if len(items)!=2:
                continue
            #item1 = mojimoji.han_to_zen(items[0])
            #item2 = mojimoji.han_to_zen(items[1])
            item1 = items[0]
            item2 = items[1]

            # 単語数があっているかどうか
            val1=item1.replace("・","　").replace("＝","　").replace(" ","　")
            val2=item2.replace("・","　").replace("＝","　").replace(" ","　")
            if len(val1.split("　")) != len(val2.split("　")):
                writeng(l)
                continue

            if is_yomigana(item1) and is_yomigana(item2):
                lstng.append(l)

            elif is_yomigana(item1) and is_midashi(item2):
                lstok.append(f"{val2}\t{jaconv.hira2kata(val1)}")
            elif is_yomigana(item2) and is_midashi(item1):
                lstok.append(f"{val1}\t{jaconv.hira2kata(val2)}")
            else:
                lstng.append(l)
  
    for l in lstok:
        write(l)
    for l in lstng:
        writeng(l)


def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="abstract_name.txt", type=str)
    parser.add_argument('--outfile', default="wikiname.txt", type=str)
    parser.add_argument('--ngfile', default="ng.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
