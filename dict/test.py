# %% [markdown]
# 辞書を異体字辞書を使って、簡単な文字に置き換える

# %%
import json

# %%
with open('itaiji.json','r',encoding='utf-8') as f:
    itaiji=json.load(f)

# %% [markdown]
# 辞書の文字を代替マップで置き換える    
# jlis.json    
# oss.json    
# crawl.json    
# seimei.json    
# 
# tankanji.json    
# statistics.json    
import copy
# %%
def replace_itaiji(fname,newfname):
    with open(fname,'r',encoding='utf-8') as f:
        jdata = json.load(f)

    newjdata={}
    for kanji,vv in jdata.items():
        newkanji=''
        for k in kanji:
            if k in itaiji:
                newkanji+=list(itaiji[k].keys())[0]
            else:
                newkanji+=k
        if newkanji not in newjdata:
            newjdata[newkanji]=vv
        else:
            for candkana,candattr in vv.items():
                tmp=newjdata[newkanji]
                for kana, attr in newjdata[newkanji].items():
                    if candkana==kana:
                        newattr=list(set(attr['dics']+candattr['dics']))
                        newjdata[newkanji][kana]={'dics':newattr}
                        break
                else:
                    newjdata[newkanji][candkana]=candattr

    with open(newfname,'w',encoding='utf-8') as f:
        json.dump(newjdata,f,ensure_ascii=False,indent=2)


# %%
replace_itaiji('jlis.json','jlis2.json')



