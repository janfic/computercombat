FROM openjdk

WORKDIR /usr/src/app

COPY build/libs/server-1.0.jar server.jar
COPY assets assets

RUN curl -L -o /mysql-connector-java-5.1.34.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.34/mysql-connector-java-5.1.34.jar
ENV CLASSPATH=/mysql-connector-java-5.1.34.jar:${CLASSPATH}

ENV AWS_DEFAULT_PROFILE=saml
ENV AWS_PROFILE=saml

CMD ["java", "-jar", "server.jar"]