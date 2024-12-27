#!/bin/env python
# coding:utf-8

import argparse
import json

def run(args):
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
      if len(item)!=2:
        continue
      lst.append(f'{item[0]},{item[1]}')
      lst.append(f'{item[1]},{item[0]}')
#      set_dic(item[0],item[1])
#      set_dic(item[1],item[0])

#  with open(args.jsonfile,'w',encoding='utf-8') as f:
#    json.dump(dic,f,indent=2,ensure_ascii=False)
  with open(args.outfile,'w',encoding='utf-8') as f:
    for l in lst:
      f.write(l+'\n')

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--infile",default="kakasi-2.3.6/itaijidict",type=str)
  parser.add_argument("--outfile",default="kakasi_itaiji.txt",type=str)
#  parser.add_argument("--jsonfile",default="kakasi_itaiji.json",type=str)
#  parser.add_argument("--dicname",default="kakasi_itaiji",type=str)
  args = parser.parse_args()
  run(args)

if __name__ == "__main__":
  main()
