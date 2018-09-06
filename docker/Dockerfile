FROM maven:3-jdk-8

COPY tjspjusbr.crt /tmp/tjspjusbr.crt

RUN yes | /usr/lib/jvm/java-8-openjdk-amd64/bin/keytool -storepass changeit -import -alias example -keystore  /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -file /tmp/tjspjusbr.crt