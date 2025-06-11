

mkdir -p data

if [ ! -f data/input.txt ];then
cat ~/src/bsa_research/kouza/meken240603/kekka/*kekka.csv > data/input.txt
fi

. ../venv/bin/activate

python exec.py --thread_num 10 --input data/input.txt --kanji_idx 1 --kana_idx 2 --outfile data/out.txt 

