
cd oss/ipadic
bash 00_wget.sh
bash 01_extract.sh
bash 02_conv.sh
cd ../mozc
bash 00_wget.sh
bash 01_extract.sh
cd ../skk
bash 00_wget.sh
bash 01_jinmei.sh
bash 02_fullname.sh
cd ..
bash merge.sh
cd ..



cd crawl
bash merge.sh
cd ..



cd itaiji
cd mozc
bash 00_wget.sh
bash 01_extract.sh
cd ..
cd skk
bash 00_wget.sh
bash 01_itaiji.sh
cd ..
cd tobunken
bash 01_wget.sh
bash 02_extract.sh
cd ..
bash merge.sh
cd ..



cd tankanji
cd mozc
bash 00_wget.sh
bash 01_extract.sh
cd ..
bash merge.sh
cd ..


cd seimei
cd n
bash copy.sh
cd ..
cd d 
bash copy.sh
cd ..
bash merge.sh
cd ..

cd statistics
bash 01_freq.sh
cd ..
