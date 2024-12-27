#!/bin/env python
# coding:utf-8

# itaiji

import argparse
import os
import glob
import pandas as pd
import re
import json

def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    for fname in glob.glob(args.indir+"/variant_rule.txt"):
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
            if len(items)!=2:
                continue
            lst.append(items[0]+","+items[1])
            lst.append(items[1]+","+items[0])

    return lst

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="mozc-2.29.5268.102/src/data/single_kanji/", type=str)
    parser.add_argument('--outfile', default="./data/extract_variant.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
