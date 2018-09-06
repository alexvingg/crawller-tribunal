FROM maven:3-jdk-8

COPY tjspjusbr.crt /tmp/tjspjusbr.crt
COPY tjmsjusbr.crt /tmp/tjmsjusbr.crt


RUN yes | /usr/lib/jvm/java-8-openjdk-amd64/bin/keytool -storepass changeit -import -alias tjspjusbr -keystore  /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -file /tmp/tjspjusbr.crt

RUN yes | /usr/lib/jvm/java-8-openjdk-amd64/bin/keytool -storepass changeit -import -alias tjmsjusbr -keystore  /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -file /tmp/tjmsjusbr.crt