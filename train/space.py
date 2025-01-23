#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
ファイル内のデータを，文字単位でスペース区切りにする
"""

import json
import argparse

def run(args):
    tgt,src=[],[]
    with open(args.infile,"r",encoding='utf-8') as f:
        src=[l.rstrip() for l in f]

    with open(args.outfile,"w",encoding="utf-8") as f:
        for s in src:
            lst=[]
            for i in range(len(s)):
                lst.append(s[i])
            t=" ".join(lst)
            f.write(f'{t}\n')
            
            
def main():
    parser = argparse.ArgumentParser(description='一列単位で作成したinfileデータを，スペース区切りにする')
    parser.add_argument('--infile', default="tmp.src", type=str)
    parser.add_argument('--outfile', default="tmp.src2", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)


if __name__ == "__main__":
    main()
