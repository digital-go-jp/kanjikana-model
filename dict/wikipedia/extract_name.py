#!/bin/env python
# coding:utf-8

import argparse
import json
import os
import re
import jaconv

# 名前のペアらしきものを抜き出す

def run(args):
    if os.path.exists(args.outfile):
        os.remove(args.outfile)

    def conv(s):
        t = str(s)
        t = re.sub('|links=no}}','}}',t)
        t = re.sub('{{.*\|', '', t)
        t = re.sub('}}','',t)
        t = t.replace("}}","")
        t = t.replace(']] ','')
        t = re.sub(']]','',t)
        t = t.replace('"',"")
        t = t.replace("&quot;","").replace("&amp;","&")
        t = t.replace("'&lt;ref&gt;","").replace("}&lt;/ref&gt;","")
        t = t.replace("'","")
        t = t.replace(",","")
        t = t.replace("、","")
        t = t.replace("本名：","")
        t = re.sub('^.*\|', "", t).replace("}}", "")
        t = re.sub('、.+$', '', t)
        t = re.sub(',.+$', '', t)
        t = re.sub('［.+］','',t)
        t = re.sub('\[.+\]', '', t)
        t = re.sub('\(.+\)', '', t)
        t = re.sub('\[\[.+：$','',t)
        t = re.sub('\[.*$','',t)
        t = re.sub(' -$','',t)
        t = t.strip()
        return t

    def midashi(l):
        items = re.findall("'''.+?'''",l)
        if len(items)<=0:
            return ""
        return conv(items[0].replace("'",""))

    def kakkonai(l):

        # カッコの中
        tmp = re.sub('^.*?（','',l)
        s = re.sub('）.*$','',tmp)
        s =re.sub(r'&lt;ref&gt;.*&lt;/ref&gt;','',s)

        #s = re.sub('&lt;.*$','',s)
        #s = re.sub('&#.*$','',s)
        s = re.sub('\[\[\d+.*$','',s)   # [[yyyy年 以降 を削除
        s = re.sub('\[.*\]','',s)
        #lst = []
        # カッコ内に{{R| .. }}を削除
        s=re.sub(r'\{\{R\|.+\}\}','',s)
        s=re.sub(r'\{\{sfn\|.+\}\}','',s)
        s=re.sub(r'\{\{Sfn\|.+\}\}','',s)
        s=re.sub(r'\{\{IPA\|.+\}\}','',s)

        # カッコないに {{ }} で囲まれた名前 {{Lang|en|Outrange}}
        items = re.findall("\{\{[Ll]ang.+?\}\}", s)
        for item in items:
            if "en" in item:
                tmp = conv(item)
                return tmp

        # カッコないの先頭が'''
        if s.find("'''")==0:
            # カッコ内に'''で囲まれた名前があるパターン
            items = re.findall("'''.+?'''", s)
            if len(items) >= 0:
                for item in items:
                    return conv(item)

        s=re.sub(r'\{\{.+\}\}','',s)


        # （から、や,まで
        #if not s.startswith("{") and s.find("、")>=0:
        #    return conv(re.sub('、.+$', '', s))

        #if not s.startswith("{") and  s.find(",")>=0:
        #    return conv(re.sub(',.+$', '', s))

        # 複数ある場合には英語を優先
        tmp =  re.split('[、,]',s)
        vals = [s.strip() for s in tmp if len(s)>0]
        if len(vals)==1:
            return vals[0]
        else:
            for val in vals:
                if re.match(r'^[a-zA-Zａ-ｚＡ-Ｚ 　・＝]+$', val) is not None:
                    return val

        return ""

    def write(l):
        with open(args.outfile,'a',encoding='utf-8') as f:
            f.write(f'{l}\n')

    def is_katakana(s):
        return re.match(r'^[ァ-ンー　・＝]+$',s) is not None

    def is_alpha(s):
        return re.match(r'^[a-zA-Zａ-ｚＡ-Ｚ 　・＝]+$',s) is not None

    with open(args.infile,'r',encoding='utf-8') as f:
        for i, l in enumerate(f):
            if i % 10000 == 0:
                print(f'i={i}')
            l = l.rstrip()
            key = midashi(l)
            val=conv(kakkonai(l))
            if key is not None and val is not None and len(key)>0 and len(val)>0:
                write(f'{key}\t{val}')

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--outfile', default="abstract_name.txt", type=str)
    parser.add_argument('--infile', default="abstract.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
