#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


"""
kakasiの異体字辞書から，異体字のペアを抜き出す。
"""

import argparse

def run(args):
  lst=[]
  with open(args.infile,'r',encoding='euc-jp') as f:
    for l in f:
      item=l.rstrip()
      if len(item)!=2:
        continue
      lst.append(f'{item[0]},{item[1]}')
      lst.append(f'{item[1]},{item[0]}')

  with open(args.outfile,'w',encoding='utf-8') as f:
    for l in lst:
      f.write(l+'\n')

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--infile",default="kakasi-2.3.6/itaijidict",type=str)
  parser.add_argument("--outfile",default="kakasi_itaiji.txt",type=str)
  args = parser.parse_args()
  run(args)

if __name__ == "__main__":
  main()
