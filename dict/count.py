import json
import argparse

def run(args):

  with open(args.infile) as f:
    jdata=json.load(f)

  cnt=0
  for k,vv in jdata.items():
    for v in vv:
      cnt+=1
  print(f"cnt={cnt}")

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--infile",default="seimei/seimei.json",type=str)
  args = parser.parse_args()
  run(args)

if __name__ == "__main__":
  main()
