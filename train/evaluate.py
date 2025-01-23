#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


import math
import argparse

def run(args):
    hsh={}
    with open(args.infile,'r',encoding='utf-8') as f:
        for l in f:
            l = l.rstrip().replace("　","")
            #print(l)
            if l.startswith('idx'):
                continue
            if not l.startswith('beam'):
                continue
            items = l.split(',')
            key = items[1]+","+items[2]
            if key not in hsh: 
                hsh[key]=[]
            pw=items[3]
            pp=items[4].replace('tensor([','').replace('])','')
            hsh[key].append([pw,math.pow(10,float(pp))])
                
    ok_prob=[]
    ok_rank=[] 
    for k,vv in hsh.items():
        items = k.split(',')
        kanji = items[0]
        kana  = items[1]
        okflg=False
        for i,v in enumerate(vv):
            if kana== v[0]:
                ok_prob.append(v[1])
                ok_rank.append(i+1)
                okflg=True
                break

    print(f'{args.infile},acc={len(ok_prob)/len(hsh)}')

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--infile',default='gen/out_r.txt')
    args = parser.parse_args()
    run(args)

if __name__ == '__main__':
    main()
