#/bin/env python
# coding:utf-8
import json
import os
import argparse
import json
import jaconv

# dicに由来をつけて，JSONにする
# {
#   "key1":{
#      "val1";{dics:["dicname"]},
#

def run(args):
    if not os.path.exists(args.jsonfile):
        jdata={}
    else:
        with open(args.jsonfile,'r',encoding='utf-8') as f:
            jdata = json.load(f)

    with open(args.infile,'r',encoding='utf-8') as f:
        for l in f:
            if l.find(',')>=0:
                items = l.rstrip().split(',')
            else:
                items = l.rstrip().split('\t')

            if len(items)<2:
                continue
            kanji = items[0]
            kana = jaconv.hira2kata(items[1])
            if len(kanji)==0 or len(kana)==0:
                continue
            if kanji not in jdata:
                jdata[kanji]={}
            if kana not in jdata[kanji]:
                jdata[kanji][kana]={}
                jdata[kanji][kana]['dics']=[args.dicname]
            else:
                jdata[kanji][kana]["dics"].append(args.dicname)
                jdata[kanji][kana]['dics']=list(set(jdata[kanji][kana]["dics"]))

    with open(args.jsonfile,'w',encoding='utf-8') as f:
        json.dump(jdata,f,indent=2,ensure_ascii=False)
        

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--infile',default='canna/dict.txt',type=str)
    parser.add_argument('--jsonfile',default='oss.json',type=str)
    parser.add_argument('--dicname', default="canna", type=str)

    args=parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)


if __name__ == '__main__':
    main()
