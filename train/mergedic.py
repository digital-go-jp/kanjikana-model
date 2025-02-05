#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
JSON形式の辞書をマージする
"""


import json
import os
import argparse
import json

def run(args):

  def load(fname):
    if not os.path.exists(fname):
      jdata={}
    else:
      with open(fname,'r',encoding='utf-8') as f:
        jdata = json.load(f)
    return jdata

  dic1 = load(args.dic1)   
  dic2 = load(args.dic2)   


  for k,vv in dic1.items():
    if k not in dic2:
      dic2[k]=vv
    else:
      for vkey,vval in vv.items():
        if vkey in dic2[k]:
          dics = list(set(dic2[k][vkey]['dics'] + vval['dics']))
          dic2[k][vkey]['dics'] = dics


  print(f'len={len(dic2)}')
  cnt=0
  for k,vv in dic2.items():
    cnt+=len(vv)
  print(f'cnt={cnt}')
  with open(args.outfile,'w',encoding='utf-8') as f:
    json.dump(dic2,f,indent=2,ensure_ascii=False)

    

def main():
    parser = argparse.ArgumentParser(description='dic1とdic2で入力された漢字とかなのペアのJSONデータをマージしする。（同じ漢字とカナのペアがあったものはマージする）')
    parser.add_argument('--dic1',default='../dict/oss/oss.json',type=str)
    parser.add_argument('--dic2',default='../dict/crawl/crawl.json',type=str)
    parser.add_argument('--outfile', default="tmp.json", type=str)

    args=parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)


if __name__ == '__main__':
    main()
