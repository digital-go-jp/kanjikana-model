#!/bin/env python
# coding:utf-8

import argparse
import json
import os
import re
import bz2

def run(args):
    if os.path.exists(args.outfile):
        os.remove(args.outfile)


    def check(l):
        # 行頭で'''
        if not l.startswith("'''"):
            return False

        if re.search('（',l) is None:
            return False

        # カッコの中
        tmp = re.sub('^.*?（','',l)
        s = re.sub('）.*$','',tmp)

        # カッコの先頭が読み
        #if not s.startswith("'''"):
        #    return False

        if re.search('\[\[\d+年\]\]',s) is None:
            return False

        if re.search('\[\[\d+月\d+日\]\]',s) is None:
            return False

        return True

    def write(l):
        with open(args.outfile,'a',encoding='utf-8') as f:
            f.write(f'{l}\n')

    with bz2.open(args.infile,'rt',encoding='utf-8') as f:
        for i, l in enumerate(f):
            if i % 1000000 == 0:
                print(f'i={i}')
            l = l.rstrip()
            if check(l):
                write(l)

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="jawiki-latest-pages-articles.xml.bz2", type=str)
    parser.add_argument('--outfile', default="abstract.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
