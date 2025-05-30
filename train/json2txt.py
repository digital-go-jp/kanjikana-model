#!/bin/env python
# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# coding:utf-8


import argparse
import json
import random
import os
random.seed(1234)


def run(args):
    with open(args.json,'r',encoding='utf-8') as f:
        jdata = json.load(f)

    lst=[]
    for k, vv in jdata.items():
        for v in vv.keys():
            if len(k)==0 or len(v)==0:
                continue
            lst.append([k,v])

    with open(args.txt,'w',encoding='utf-8') as f:
        for t in lst:
            f.write(t[0]+','+t[1]+'\n')



# %%

def main():
    parser = argparse.ArgumentParser(description="json辞書ファイルをTXTへconvert")

    parser.add_argument("--json", default="tmp.json", type=str)
    parser.add_argument("--txt", default="tmp.txt", type=str)


    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
