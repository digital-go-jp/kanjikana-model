uname=`uname`


#cp ../core/version.txt .
VER=`cat ../core/version.txt`
echo VER=$VER

mkdir -p lib
rm -rf lib/kanjikana_core-${VER}-jar-with-dependencies.jar
cp -rp ../core/lib/kanjikana_core-${VER}-jar-with-dependencies.jar lib/


rm -rf target

if [ "$uname" = "Darwin" ];then
cat pom.xml.template.mac|sed "s/VER/$VER/"|sed "s/WEB/$VER/" > pom.xml
else
cat pom.xml.template|sed "s/VER/$VER/"|sed "s/WEB/$VER/" > pom.xml
fi

#mvn dependency:copy-dependencies -DoutputDirectory=lib
mvn clean compile assembly:single
mvn javadoc:javadoc

nfname=`ls target/kanjikana_api-${VER}-jar-with-dependencies.jar`
zip -d $nfname jp/go/digital/kanjikana/core/executor/generate/Kana2KanjiMain.class
zip -d $nfname jp/go/digital/kanjikana/core/executor/generate/Kanji2KanaMain.class
zip -d $nfname jp/go/digital/kanjikana/core/executor/match/KanjiKanaMatchMain.class

cp -pr target/kanjikana_api-${VER}-jar-with-dependencies.jar lib/.
