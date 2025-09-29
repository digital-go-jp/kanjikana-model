
import multiprocessing
import argparse
import os.path
import urllib.request
import datetime
import json
import os

def split_array(ar, n_group):
    for i in range(n_group):
        yield ar[i * len(ar) // n_group:(i+1)*len(ar) // n_group]
def exec(**kwargs):
    if 'kwargs' in kwargs:
        procid=kwargs['kwargs']['procid']
        url=kwargs['kwargs']['url']
        kklst=kwargs['kwargs']['kklst']
        outfile=kwargs['kwargs']['outfile']
    else:
        procid=kwargs['procid']
        url=kwargs['url']
        kklst=kwargs['kklst']
        outfile=kwargs['outfile']
    fname=f'{outfile}.{procid}'
    if os.path.exists(fname):
        os.remove(fname)

    for kk in kklst:
        stdate = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f')[:-3]
        kanji,kana=kk.split(',')
        params={'kanji':kanji,'kana':kana}
        req = urllib.request.Request('{}?{}'.format(url,urllib.parse.urlencode(params)))
        with urllib.request.urlopen(req) as res:
            body = res.read()
            jdata = json.loads(body)
        if 'result' in jdata:
            stat = jdata['result']['status']
        else:
            stat = "error"
        eddate = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f')[:-3]
        with open(fname,'a',encoding='utf-8') as f:
            f.write(f'{kk},{stat},{stdate},{eddate}\n')

def run(args):
    lst = []  # kanji,kana
    with open(args.input, 'r', encoding='utf-8') as f:
        for l in f:
            items = l.rstrip().split(',')
            lst.append(f'{items[args.kanji_idx]},{items[args.kana_idx]}')
    if args.thread_num<=1:
        exec(kwargs={'procid':0,'url':args.url,'kklst':lst,'outfile':args.outfile})
    else:
        proc=[]
        splited=split_array(lst,args.thread_num)
        for i,lst in enumerate(splited):
            p=multiprocessing.Process(target=exec,kwargs={'procid':i,'url':args.url,'kklst':lst,'outfile':args.outfile})
            proc.append(p)
        for p in proc:
            p.start()
        print('Process started.')
        for p in proc:
            p.join()
        print('Process joined.')
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input",default="data/input.txt", type=str)
    parser.add_argument('--kanji_idx', default=1, type=int)
    parser.add_argument('--kana_idx', default=2, type=int)
    parser.add_argument('--thread_num', default=2, type=int)
    parser.add_argument('--url', default='http://localhost:8080/detail', type=str)
    parser.add_argument('--outfile', default='data/out.txt', type=str)

    args = parser.parse_args()
    run(args)

if __name__ == '__main__':
    main()
