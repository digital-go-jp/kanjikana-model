#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


"""
２つの漢字・アルファベットとカタカナの入ったファイルから，JSONL形式に変換する
"""

import argparse
import json

def run(args):
    with open(args.tgt,'r',encoding='utf-8') as f:
        tgt_lst = [s.rstrip().replace(" ",'') for s in f]

    with open(args.src,'r',encoding='utf-8') as f:
        src_lst = [s.rstrip().replace(" ",'') for s in f]

    jdata=[]
    for t,s in zip(tgt_lst,src_lst):
        jdata.append({args.key:{args.src_key:s, args.tgt_key:t}})
    with open(args.outfile,'w',encoding='utf-8') as f:
        for l in jdata:
            f.write(json.dumps(l,ensure_ascii=False)+'\n')

def main():
    parser = argparse.ArgumentParser(description="tgt,srcで指定された２つのファイルから，JSONL形式に変換する")
    parser.add_argument("--tgt",default="dataset/train.tgt", type=str)
    parser.add_argument("--src",default="dataset/train.src", type=str)
    parser.add_argument("--tgt_key",default="kana", type=str, help="tgtのデータにつけるキー")
    parser.add_argument("--src_key",default="kanji", type=str, help="srcのデータにつけるキー")
    parser.add_argument("--key",default="translation", type=str, help="src,tgtの辞書形式につけるキー")
    parser.add_argument("--outfile",default="dataset/train.jsonl", type=str)

    args = parser.parse_args()
    run(args)

if __name__ == '__main__':
    main()
