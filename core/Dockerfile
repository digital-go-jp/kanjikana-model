FROM  amazonlinux:2023

#RUN dnf -y install vim nkf

RUN dnf -y install glibc-langpack-ja
RUN dnf -y install java-17-amazon-corretto-devel

COPY .djl.ai/ /root/
#COPY conf/log4j2.xml /log4j2.xml
#COPY target/kanjikana_model-1.0-jar-with-dependencies.jar /app.jar
#ENTRYPOINT ["java","-Xmx4096M","-Dlog3j.configurationFile=/log4j2.xml","-jar","/app.jar" ]
#ENTRYPOINT ["java","-Xmx4096M","-Dlog4j.configurationFile=/log4j2.xml","-jar","/app.jar" ]

