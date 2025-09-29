
. ../../venv/bin/activate

rm -rf tankanji.json

# dicnameはjp.go.digital.kanjikana.core.engine.dict.DictTypeと合わせる

python merge.py --infile mj/mj.txt --jsonfile tankanji.json --dicname mj_tankanji
#python merge.py --infile mozc/extract_single.txt --jsonfile tankanji.json --dicname mozc_tankanji



