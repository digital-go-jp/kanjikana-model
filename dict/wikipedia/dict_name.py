#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
select_name.pyによってWikipediaのダンプファイルから作成した漢字・アルファベットとカタカナのペアを単語単位に分割する
"""

import argparse
import json
import mojimoji

def run(args):
    lst=[]
    with open(args.infile,'r',encoding='utf-8') as f:
        for l in f:
            kanjis,kanas = l.rstrip().split("\t")
            for kanji,kana in zip(kanjis.replace("　",' ').split(" "),kanas.replace("　",' ').split(" ")):
                if mojimoji.han_to_zen(kanji)==kana:
                    continue
                lst.append(f'{kanji},{kana}')
    lst=list(set(lst))
    with open(args.outfile,'w',encoding='utf-8') as f:
        for l in lst:
            f.write(f"{l}\n")

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="wikiname.txt", type=str)
    parser.add_argument('--outfile', default="dict.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
