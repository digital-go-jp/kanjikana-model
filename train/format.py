#!/bin/env python
# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# coding:utf-8

import argparse
import json

def run(args):
    with open(args.tgt,'r',encoding='utf-8') as f:
        tgt_lst = [s.rstrip().replace(" ",'') for s in f]

    with open(args.src,'r',encoding='utf-8') as f:
        src_lst = [s.rstrip().replace(" ",'') for s in f]

    jdata=[]
    for t,s in zip(tgt_lst,src_lst):
        jdata.append({'translation':{'kanji':s, 'kana':t}})
    with open(args.outfile,'w',encoding='utf-8') as f:
        for l in jdata:
            f.write(json.dumps(l,ensure_ascii=False)+'\n')

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--tgt",default="dataset/train.tgt", type=str)
    parser.add_argument("--src",default="dataset/train.src", type=str)
    parser.add_argument("--outfile",default="dataset/train.jsonl", type=str)

    args = parser.parse_args()
    run(args)

if __name__ == '__main__':
    main()
