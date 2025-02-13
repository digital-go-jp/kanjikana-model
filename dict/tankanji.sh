#!/bin/bash

. ../venv/bin/activate
rm -rf tankanji.json

# dicnameはjp.go.digital.kanjikana.core.engine.dict.DictTypeと合わせる
dics="
canna
freewnn
ipadic
kakasi
mozc
neologd
skk
mj
"

for dic in $dics;do
  if [ -f $dic/tankanji.txt ];then
    python merge.py --infile $dic/tankanji.txt --jsonfile tankanji.json --dicname $dic
  fi 
done
