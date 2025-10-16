FROM public.ecr.aws/amazonlinux/amazonlinux:2023

RUN dnf -y install glibc-langpack-ja
RUN dnf -y install java-21-amazon-corretto-devel
COPY conf/log4j2.xml /log4j2.xml
COPY lib/kanjikana_api-1.8o-jar-with-dependencies.jar /app.jar
COPY .djl.ai/ /root/.djl.ai/
COPY .cache/ /root/.cache/
ENTRYPOINT ["java","-Xmx8192M","-Dlog4j.configurationFile=/log4j2.xml","-jar","/app.jar" ]
