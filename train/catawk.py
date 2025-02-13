#!/bin/env python
# coding:utf-8

# Copyright (c) 2025 デジタル庁
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

"""
入力されたCSVデータの列位置を指定して，ファイルの行の上位からのスタート位置％とエンド位置％の間のデータを，指定されたファイルに追加する
"""

import argparse

def run(args):
    delim=','
    if args.infile_delimiter=='tsv':
        delim='\t'
    lst=[]
    with open(args.infile,'r',encoding='utf-8') as f:
        for l in f:
            lst.append(l.rstrip().split(delim)[args.index])

    siz=len(lst)
    bidx=int(siz*args.begin_idx_per/100)
    eidx=int(siz*args.end_idx_per/100)
    with open(args.appendfile,'a',encoding='utf-8') as f:
        for l in lst[bidx:eidx]:
            f.write(l+'\n')

def main():
    # 引数の処理
    parser = argparse.ArgumentParser(description='漢字とかなのカンマ区切のテキストデータを，列位置を指定して追加する')

    parser.add_argument('--infile', default='../dict/wikipedia/wikiname.txt', type=str, help="入力ファイル")
    parser.add_argument('--begin_idx_per', default=0, type=float, help="ファイルから抜き出す先頭の位置，ファイル全体行からのパーセント")
    parser.add_argument('--end_idx_per', default=100, type=float, help="ファイルから抜き出す末尾の位置，ファイル全体行からのパーセント")
    parser.add_argument('--appendfile', default="dataset/train.src", type=str)
    parser.add_argument('--index', default=0, type=int, help="漢字，カナで構成されるCSVファイルの何列目を抜き出すか？０オリジン")
    parser.add_argument('--infile_delimiter', default='csv', choices=('csv','tsv'), help="infileファイルの区切り文字")

    args = parser.parse_args()
    run(args)

if __name__ == '__main__':
    main()