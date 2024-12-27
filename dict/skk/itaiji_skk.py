#!/bin/env python
# coding:utf-8

import json
import argparse
import re
class Namae:
    def __init__(self, args):
        self.args = args
        self.hsh={}

    def run(self):
        with open(self.args.infile,"r",encoding="utf-8") as f:
            for l in f:
                l=l.rstrip()
                if l.find(";;")==0:
                    continue

                items=l.split(" ")
                if len(items)!=2:
                    continue

                self.hsh[items[0]]=[]
                for keys in items[1].split("/"):
                    tmp=re.sub(";.*$","",keys)
                    if len(tmp)>0:
                        self.hsh[items[0]].append(tmp)

        res={}
        for k,vv in self.hsh.items():
            if k not in res:
                res[k]=[]
            res[k]+=vv
            for v in vv:
                if v not in res:
                    res[v]=[]
                if v in self.hsh:
                    res[v]+=list(self.hsh[v])

        hsh={}
        for k,v in res.items():
            if k in v:
                v.remove(k)
            hsh[k]=list(set(v))

        lst=[]
        for k,vv in hsh.items():
            for v in vv:
                lst.append(f'{k},{v}')
                lst.append(f'{v},{k}')
    
        lst = list(set(lst))

        with open(self.args.outfile,"w",encoding="utf-8") as f:
            for l in lst:
                f.write(f'{l}\n')

def main():
    # 引数の処理
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="SKK-JISYO.itaiji", type=str)
    parser.add_argument('--outfile', default="itaiji_skk.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    Namae(args).run()

if __name__ == "__main__":
    main()
