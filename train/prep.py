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

    k_max_len=0
    v_max_len=0
    lst=[]
    for k, vv in jdata.items():
        for v in vv.keys():
            if len(k)==0 or len(v)==0:
                continue
            lst.append([k,v])
            k_max_len=k_max_len if k_max_len>len(k) else len(k)
            v_max_len=v_max_len if v_max_len>len(v) else len(v)

    print(f'k_max_len={k_max_len}')
    print(f'v_max_len={v_max_len}')
    random.shuffle(lst)
    adr=int(len(lst)*args.ratio)

    val = lst[:adr]
    test = lst[adr:adr*2]

    train = lst[adr*2:]

    os.makedirs(args.outdir,exist_ok=True)
    def write(fname,ary):
        print(f'{fname},{len(ary)}')
        with open(args.outdir+"/"+fname+".src",'w',encoding='utf-8') as f:
            for t in ary:
                f.write(t[0]+'\n')
        with open(args.outdir+"/"+fname+".tgt",'w',encoding='utf-8') as f:
            for t in ary:
                f.write(t[1]+'\n')

    write("train",train)
    write("valid",val)
    write("test",test)



# %%

def main():
    parser = argparse.ArgumentParser(description="")

    parser.add_argument("--json", default="tmp.json", type=str)
    parser.add_argument("--outdir", default="dataset", type=str)
    parser.add_argument("--ratio", default=0.01, type=float)


    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
