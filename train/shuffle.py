#!/bin/env python
# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# coding:utf-8
import json
import argparse
import pandas as pd
import numpy as np

def run(args):
    tgt,src=[],[]
    with open(args.tgt,"r",encoding='utf-8') as f:
        tgt=[l.rstrip() for l in f]

    with open(args.src,"r",encoding='utf-8') as f:
        src=[l.rstrip() for l in f]

    df=pd.DataFrame({"src":src,"tgt":tgt})

    # omit blank 
    df=df.replace(r'^\s*$',np.nan, regex=True)
    df=df.dropna()

    df=df.sample(frac=1,random_state=1)
    with open(args.tgt,'w',encoding='utf-8') as f:
        for s in df['tgt']:
            f.write(f'{s}\n')
            
    with open(args.src,'w',encoding='utf-8') as f:
        for s in df['src']:
            f.write(f'{s}\n')
            
def main():
    parser = argparse.ArgumentParser(description='入力したsrcとtgtデータを，それぞの同じ行が同じ行になるように行単位でシャッフルする')
    parser.add_argument('--tgt', default="dataset/train.src", type=str)
    parser.add_argument('--src', default="dataset/train.tgt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)


if __name__ == "__main__":
    main()
