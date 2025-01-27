#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
mozcの単漢字と読み仮名から，単漢字辞書を抽出する。
"""

import argparse
import os
import glob
import json

def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    for fname in glob.glob(args.indir+"/single_kanji.tsv"):
        lst=extract(fname)

        with open(args.outfile,"w",encoding="utf-8") as f:
            for l in lst:
                f.write(l+"\n")

def extract(file):
    print(file)
    lst=[]
    with open(file,"r",encoding="utf-8") as f:
        for l in f:
            if l.startswith("#"):
                continue
            items=l.rstrip().split('\t')
            for i in range(len(items[1])):
                lst.append(items[1][i]+","+items[0])

    return lst

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="mozc-2.29.5268.102/src/data/single_kanji/", type=str)
    parser.add_argument('--outfile', default="./data/extract_single.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
