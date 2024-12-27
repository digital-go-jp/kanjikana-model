#!/bin/env python
# coding:utf-8

import argparse
import os
import json
import jaconv

def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    lst = []
    with open(args.infile,'r',encoding='euc-jp') as f:
        for l in f:
            items = l.rstrip().split(" ")
            kanji=items[1]
            kana=jaconv.hira2kata(items[0])
            lst.append(f'{kanji},{kana}')

    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in lst:
            f.write(l+"\n")
def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="FreeWnn-1.1.1-a023/PubdicPlus/pubdic.p", type=str)
    parser.add_argument('--outfile', default="./dict.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
