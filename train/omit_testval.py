#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


"""
開発，検証用に，訓練データに入っているものがあれば，削除する(インサンプルを阻止)
"""

import json
import argparse
import pandas as pd

def load(fname):
    with open(fname,'r',encoding='utf-8') as f:
        ary=[l.rstrip() for l in f]
    return ary

def run(args):
    train_src=load(args.train_src)
    train_tgt=load(args.train_tgt)
    valid_src=load(args.valid_src)
    valid_tgt=load(args.valid_tgt)
    test_src=load(args.test_src)
    test_tgt=load(args.test_tgt)

    hsh={}
    for s,t in zip(valid_src,valid_tgt):
        hsh[s.replace(" ","")+"_"+t.replace(" ","")]=1
    for s,t in zip(test_src,test_tgt):
        hsh[s.replace(" ","")+"_"+t.replace(" ","")]=1

    print(f"train={len(train_src)}")
    src=[]
    tgt=[]
    cnt=0
    for s,t in zip(train_src,train_tgt):
        key=s+"_"+t
        if key in hsh:
            cnt+=1
            continue
        src.append(s)
        tgt.append(t)

    print(f"omit={cnt}")
    with open(args.train_src,'w',encoding='utf-8') as f:
        for s in src:
            f.write(s+"\n")

    with open(args.train_tgt,'w',encoding='utf-8') as f:
        for t in tgt:
            f.write(t+"\n")
            
def main():
    parser = argparse.ArgumentParser(description='test_src,test_tgtのペア及びval_src,val_tgtのペアのデータのうち，train_src,traing_tgtに同じものがあれば，削除する')
    parser.add_argument('--train_src', default="dataset/train.src", type=str)
    parser.add_argument('--train_tgt', default="dataset/train.tgt", type=str)
    parser.add_argument('--test_src', default="dataset/test.src", type=str)
    parser.add_argument('--test_tgt', default="dataset/test.tgt", type=str)
    parser.add_argument('--valid_src', default="dataset/valid.src", type=str)
    parser.add_argument('--valid_tgt', default="dataset/valid.tgt", type=str)
    
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)


if __name__ == "__main__":
    main()
