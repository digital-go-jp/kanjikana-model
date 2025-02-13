#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
Cannaから辞書ファイルを抽出し，漢字・アルファベットとカタカナのペアを作成する。地名などを含んでいる
"""

import argparse
import os
import json
import jaconv
import glob

def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    lst = []
    for fname in glob.glob(args.indir+"/*.t"):
        if os.path.basename(fname) not in ["chimei.t","number.t","software.t","suffix.t"]:
            continue
        with open(fname,'r',encoding='euc-jp') as f:
            for l in f:
                items = l.rstrip().split(" ")
                kana=jaconv.hira2kata(items[0])
                if kana.startswith("#"):
                    continue
                for item in items[1:]:
                    if item.startswith("#"):
                        continue
                    lst.append(f'{item},{kana}')
    lst = list(set(lst))
    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in lst:
            f.write(l+"\n")
def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="Canna37p3/dic/ideo/words", type=str)
    parser.add_argument('--outfile', default="./word.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
