#!/bin/env python
# coding:utf-8



import argparse
import os
import glob
import pandas as pd
import re
import json
import jaconv
import regex

def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)
#    with open(args.outfile,"w",encoding="utf-8") as f:
#        f.write("kanji\tkana\n")

    hsh={}
    def extract(file):
        print(file)
        with open(file,"r",encoding="utf-8") as f:
            for l in f:
                if l.startswith("#"):
                    continue
                items=l.rstrip().split()
                if jaconv.hira2kata(items[1])!=jaconv.hira2kata(items[0]):
                    k = jaconv.hira2kata(items[0])
                    if regex.match('^\p{Katakana}+$', k) is None:
                        continue
                    hsh[items[1]+","+k]=1
                if jaconv.hira2kata(items[3])!=jaconv.hira2kata(items[2]):
                    k = jaconv.hira2kata(items[2])
                    if regex.match('^\p{Katakana}+$', k) is None:
                        continue
                    hsh[items[3] + "," + k]=1

    for fname in glob.glob(args.indir+"/aux_dictionary.tsv"):
        extract(fname)

    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in hsh.keys():
            f.write(l+"\n")


def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="mozc-2.29.5268.102/src/data/oss/", type=str)
    parser.add_argument('--outfile', default="./extract_oss.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
