FROM ubuntu:latest 

WORKDIR /usr/src/app

ENV DEBIAN_FRONTEND noninteractive

# Install Git and Java
RUN apt-get -y update
RUN apt-get -y install git
RUN apt-get -y install wget
RUN apt-get -y install unzip
RUN apt-get -y install openjdk-11-jdk

# Install Gradle
ENV VERSION=6.3
RUN wget https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-${VERSION}-bin.zip
RUN ln -s /opt/gradle/gradle-${VERSION} /opt/gradle/latest
ENV GRADLE_HOME=/opt/gradle/latest
ENV PATH=${GRADLE_HOME}/bin:${PATH}

ENV AWS_DEFAULT_PROFILE=saml
ENV AWS_PROFILE=saml

EXPOSE 7272

COPY ./deploy_server.sh server.sh

ENTRYPOINT ./server.sh