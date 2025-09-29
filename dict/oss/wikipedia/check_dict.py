#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
ossで指定したオープンソースの辞書のデータに，infileのデータのうち入っていない漢字とカナのペアを抜き出して，outfileに出力する
"""


import argparse
import json

def run(args):
    hsh={}
    with open(args.oss,'r',encoding='utf-8') as f:
        hsh=json.load(f)

    cnt=0
    total=0
    lst=[]
    with open(args.infile,'r',encoding='utf-8') as f:
        for l in f:
            total+=1
            kanji,kana = l.rstrip().split(",")
            if kanji in hsh:
                if kana in hsh[kanji]:
                    cnt+=1
                    continue
            lst.append(l)
    print(f'total={total},cnt={cnt}')

    with open(args.outfile,'w',encoding='utf-8') as f:
        for l in lst:
            f.write(f'{l}')


def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="dict.txt", type=str)
    parser.add_argument('--outfile', default="dict_add.txt", type=str)
    parser.add_argument('--oss', default="../oss.json", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
