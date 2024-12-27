#!/bin/env python
# coding:utf-8

import argparse
import json
import jaconv
import re

def run(args):
  hiragana = re.compile('[ぁ-ゟ]+')
  dic={}

  def set_dic(key,val):
    if key not in dic:
      dic[key] = {}
    if val not in dic[key]:
      dic[key][val] = {"dics": [args.dicname]}
    else:
      dic[key][val]["dics"].append(args.dicname)
      dic[key][val]["dics"]=list(set(dic[key][val]["dics"]))

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

#      set_dic(kanji,kana)

#  with open(args.jsonfile,'w',encoding='utf-8') as f:
#    json.dump(dic,f,indent=2,ensure_ascii=False)

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--infile",default="kakasi-2.3.6/kakasidict",type=str)
#  parser.add_argument("--jsonfile",default="kakasi.json",type=str)
  parser.add_argument("--outfile",default="kakasi.txt",type=str)
#  parser.add_argument("--dicname",default="kakasi",type=str)
  args = parser.parse_args()
  run(args)

if __name__ == "__main__":
  main()
