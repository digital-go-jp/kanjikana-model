
import jaconv
import json
dic={}
with open("mj.tsv",'r',encoding='utf-8') as f:
    for l in f:
        items = l.rstrip().split("\t")
        if len(items)!=2:
            continue
        if len(items[0])!=1:
            continue

        kitems=items[1].split("・")
        lst = []
        for k in kitems:
            if len(k)>0:
                lst.append(jaconv.hira2kata(k))
        if len(lst)==0:
            continue
        if items[0] not in dic:
            dic[items[0]]=[]
        dic[items[0]]+=lst

cnt=0
for k,vv in dic.items():
    cnt+=len(vv)
print(f'cnt={cnt}')

print(f'len={len(dic)}')

with open("mj.txt",'w',encoding='utf-8') as f:
    for k,vv in dic.items():
        for v in vv:
            f.write(f'{k},{v}\n')
