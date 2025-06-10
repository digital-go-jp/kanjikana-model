#!/bin/env python
# coding:utf-8

import argparse
from lxml import html, etree
import os
import json
import copy


class Namae:
    def __init__(self, args):
        self.args = args

    def run(self):
        hsh={}
        with open(self.args.infile, "r", encoding="utf-8") as f:
            t = "".join(f.readlines())
        x = etree.HTML(t)
        ele = x.findall(".//div[@id='content']//table/tbody/tr")

        for e in ele:
            td = e.findall("./td")
            if len(td) == 2:
                hsh[td[0].text]=td[1].text.split(" ")

        # キーとValが同じならば削除
        res={}
        for k,v in hsh.items():
            vals=[]
            for vv in v:
                if vv == k:
                    continue
                vals.append(vv)
            res[k]=vals

        # valで他のキーと同じならば，Valを追加する
        res2={}
        for k,v in res.items():
            vals=[]
            for vv in v:
                vals.append(vv)
                if vv in res:
                    vals+=res[vv]
            lst=list(set(vals))
            if k in lst:
                lst.remove(k)
            if len(lst)>0:
                res2[k]=lst

        # すべての文字でキーを作る
        res3={}
        for k,v in res2.items():
            res3[k]=v
            for vv in v:
                if vv not in res2:
                    tmp=copy.deepcopy(v)
                    tmp.remove(vv)
                    tmp.append(k)
                    res3[vv]=tmp

        with open(self.args.outfile,"w",encoding="utf-8") as f:
            for k,vv in res3.items():
                for v in vv:
                    f.write(f'{k},{v}\n')

def main():
    # 引数の処理
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="index.html", type=str)
    parser.add_argument('--outfile', default="itaiji.json", type=str)

    args = parser.parse_args()

    Namae(args).run()


if __name__ == "__main__":
    main()
