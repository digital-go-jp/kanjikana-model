#/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

import argparse
import os
import json


def run(args):
    if len(os.path.dirname(args.outfile))>0:
        os.makedirs(os.path.dirname(args.outfile),exist_ok=True)

    lst = []
    with open(args.infile1,'r',encoding='utf-8') as f:
        for l in f:
            lst.append(l)

    with open(args.infile2,'r',encoding='utf-8') as f:
        for l in f:
            lst.append(l)

    with open(args.outfile,"w",encoding="utf-8") as f:
        for l in lst:
            f.write(l)
def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile1', default="dict.txt", type=str)
    parser.add_argument('--infile2', default="dict.txt", type=str)
    parser.add_argument('--outfile', default="tankanji.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()

