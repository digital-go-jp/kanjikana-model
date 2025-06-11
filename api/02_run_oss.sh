
VER=`cat ../core/version.txt`o
echo VER=$VER


cat Dockerfile.template|sed "s/WEB/$VER/"|sed s/kanjikana_api-/kanjikana_api_oss-/ > Dockerfile
docker build  -t kanjikanaapi_oss:$VER .
docker rm kanjikanaapi_oss
docker run --name kanjikanaapi_oss -p 8081:8080 -it kanjikanaapi_oss:$VER

