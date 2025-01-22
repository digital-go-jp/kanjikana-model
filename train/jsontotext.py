# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


import jaconv
import argparse
import json

def run(args):

    lst=[]
    with open(args.jsonfile,'r',encoding='utf-8') as f:
        jdata = json.load(f)

    for kanji,hh in jdata.items():
        for kana in hh.keys():
            if args.reverse:
                lst.append(f'{kana},{kanji}')
            else:
                lst.append(f'{kanji},{kana}')

    with open(args.outfile,'w',encoding='utf-8') as f:
        for l in lst:
            f.write(f'{l}\n')


def main():
    parser = argparse.ArgumentParser(description="jsonで作成された辞書データを，CSV形式へ変換する")
    parser.add_argument("--jsonfile", default="../dict_oss/tankanji.json", type=str)
    parser.add_argument("--outfile", default="tankanji.txt", type=str)
    parser.add_argument("--reverse", action="store_true",help="trueのとき、カタカナから漢字を学習するデータセットを作成する")


    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
