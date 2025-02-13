#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
skkの姓名辞書の漢字・アルファベットと読み仮名から，漢字・アルファベットとカタカナのペアを作成する。
"""

import json
import argparse
import re
import jaconv

KOMOJI=['ァ','ィ','ゥ','ェ','ォ','ッ','ャ','ュ','ョ','ヮ','ヵ','ヶ']
OMOJI= ['ア','イ','ウ','エ','オ','ツ','ヤ','ユ','ヨ','ワ','カ','ケ']

def conv(s):
    for k,o in zip(KOMOJI,OMOJI):
        s=s.replace(k,o)
    return s

class Namae:
    def __init__(self, args):
        self.args = args

    def run(self):
        hsh={}
        with open(self.args.infile,"r",encoding="euc-jp") as f:
            for l in f:
                l=l.rstrip()
                if l.find(";;")==0:
                    continue

                items=l.split(" ")
                if len(items)!=2:
                    continue
                yomi=items[0]
                kanji_lst=[]
                for kanji in items[1].split("/"):
                    if kanji.find("＊")>=0:
                        continue
                    if kanji.find("フルネーム")>=0:
                        continue
                    if kanji.find(",")>=0:
                        continue
                    tmp=re.sub(";.*$","",kanji)
                    if len(tmp)>0:
                        if tmp!=yomi:
                            kanji_lst.append(tmp)
                for kanji in kanji_lst:
                    if re.match(r'^[\u30A0-\u30FF]+$', kanji) is not None:
                        continue
                    if kanji not in hsh:
                        hsh[kanji]=[]
                    kyomi=jaconv.hira2kata(yomi)
                    hsh[kanji].append(kyomi)

        with open(self.args.outfile,"w",encoding="utf-8") as f:
            for k,vv in hsh.items():
                for v in vv:
                    f.write(f'{k},{v}\n')

def main():
    # 引数の処理
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="data/SKK-JISYO.jinmei", type=str)
    parser.add_argument('--outfile', default="data/jinmei.json", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    Namae(args).run()

if __name__ == "__main__":
    main()
