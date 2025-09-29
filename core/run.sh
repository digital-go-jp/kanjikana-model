
JAR=`ls mnt/*jar`

java -Xmx8192M -Dlog4j.configurationFile=mnt/conf/log4j2.xml  -classpath $JAR jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatchMain --infile mnt/input.txt --outfile mnt/output.txt --has_header False  --kanji_idx 1 --kana_idx 2 --strategy ENSEMBLE
