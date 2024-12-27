
import jaconv
import csv

def run(args):
    dic={}
    with open(args.infile,'r',encoding='utf-8') as f:
        for i, items in enumerate(csv.reader(f)):
            if i == 0:
                continue
            if len(items)<28:
                continue
            if len(items[1])!=1:
                continue
            kana=items[28]
            kanji=items[1]

            kitems=kana.split("・")
            lst = []
            for k in kitems:
                if len(k)>0:
                    lst.append(jaconv.hira2kata(k))
            if len(lst)==0:
                continue
            if kanji not in dic:
                dic[kanji]=[]
            dic[kanji]+=lst

    cnt=0
    for k,vv in dic.items():
        cnt+=len(vv)
    print(f'cnt={cnt}')
    print(f'len={len(dic)}')

    with open(args.outfile,'w',encoding='utf-8') as f:
        for k,vv in dic.items():
            for v in vv:
                f.write(f'{k},{v}\n')

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('--infile', default="mj.csv", type=str)
    parser.add_argument('--outfile', default="tankanji.txt", type=str)
    args = parser.parse_args()
    print(json.dumps(args.__dict__, indent=2))
    run(args)

if __name__ == "__main__":
    main()