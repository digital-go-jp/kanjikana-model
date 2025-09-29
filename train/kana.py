# Copyright (c) 2024 デジタル庁
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


import jaconv
import argparse
import json

def run(args):

    lst=[]
    hiragana = [chr(i) for i in range(ord("ぁ"), ord("ゖ")+1)]
    for h in hiragana:
        lst.append([jaconv.hira2kata(h),jaconv.hira2kata(h)])
        lst.append([h,jaconv.hira2kata(h)])

    hsh={}
    for kanji,kana in lst:
        if kanji not in hsh:
            hsh[kanji]=[]
        hsh[kanji].append(kana)

    with open(args.outfile,'w',encoding='utf-8') as f:
        for k,vv in hsh.items():
            for v in vv:
                f.write(f'{k},{v}\n')


def main():
    parser = argparse.ArgumentParser(description="")
    parser.add_argument("--outfile", default="hirakata.txt", type=str)


    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()
