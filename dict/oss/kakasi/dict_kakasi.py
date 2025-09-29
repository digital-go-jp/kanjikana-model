#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT



"""
kakasiの漢字かな変換辞書から，，漢字・アルファベットとカタカナのペアを作成する。氏名以外のものも含まれる。
"""

import argparse
import jaconv
import re

def run(args):
  hiragana = re.compile('[ぁ-ゟ]+')

  lst=[]
  with open(args.infile,'r',encoding='euc-jp') as f:
    for l in f:
      item=l.rstrip()
      if item.startswith(";"):
          continue
      items = item.split(" ")
      if len(items)!=2:
        continue
      if not hiragana.fullmatch(items[0]):
        continue
      kana=jaconv.hira2kata(items[0])
      kanji=items[1]
      lst.append(f'{kanji},{kana}')
  with open(args.outfile,'w',encoding='utf-8') as f:
    for l in lst:
       f.write(l+'\n')

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--infile",default="kakasi-2.3.6/kakasidict",type=str)
  parser.add_argument("--outfile",default="kakasi.txt",type=str)
  args = parser.parse_args()
  run(args)

if __name__ == "__main__":
  main()
