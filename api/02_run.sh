
VER=`cat ../core/version.txt`
echo VER=$VER


mkdir -p data
cat Dockerfile.template|sed "s/WEB/$VER/" > Dockerfile
docker build  -t kanjikanaapi:$VER .
docker rm kanjikanaapi --force
docker run --name kanjikanaapi -p 8080:8080 -it -v $PWD/data:/mnt kanjikanaapi:$VER



