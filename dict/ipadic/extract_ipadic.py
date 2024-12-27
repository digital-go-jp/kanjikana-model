#!/bin/env python
# coding:utf-8



import argparse
import os
import glob
import regex
import jaconv
import json

kanji_pattern=regex.compile(r'[\p{Script=Latin}\p{Script_Extensions=Han}\p{Script=Katakana}\p{Script=Hiragana}]+')
kana_pattern=regex.compile(r'\p{Script=Katakana}+')
def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)
#    with open(args.outfile,"w",encoding="utf-8") as f:
#        f.write("kanji\tkana\n")
    lst = []
    for fname in sorted(glob.glob(args.indir+"/*.csv")):
        lst+=extract(fname)
    
    lst = sorted(list(set(lst)))
    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in lst:
            f.write(l+"\n")

def extract(file):
    print(file)
    lst=[]
    with open(file,"r",encoding="euc-jp") as f:
        for l in f:
            items = l.rstrip().split(",")
            if items[0]!=items[10]:
                continue
            kanji=items[10]
            yomi1=jaconv.hira2kata(items[11])
            if items[0].startswith("#"):
                continue
            if not kana_pattern.fullmatch(yomi1):
                continue
            if not kanji_pattern.fullmatch(kanji):
                continue
            lst.append(f'{kanji},{yomi1}')
            #if yomi1!=yomi2:
            #    lst.append(f'{kanji}\t{yomi2}')

    return lst

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--indir', default="mecab-ipadic-neologd-0.0.7/seed", type=str)
    parser.add_argument('--outfile', default="dict.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
