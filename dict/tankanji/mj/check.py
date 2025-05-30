
import json
with open('mj.json','r',encoding='utf-8') as f:
  mj=json.load(f)

siz={}
for k in mj.keys():
  if len(k) not in siz:
    siz[len(k)]=0
  siz[len(k)]+=1

print(siz)
