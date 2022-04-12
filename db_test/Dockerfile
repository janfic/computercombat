FROM ubuntu/mysql

WORKDIR /usr/src/app

ENV DEBIAN_FRONTEND noninteractive

COPY create_computer_combat.sql /docker-entrypoint-initdb.d/create_computer_combat.sql
COPY data.sql /docker-entrypoint-initdb.d/data.sql
COPY users.sql /docker-entrypoint-initdb.d/users.sql

ENV TZ UTC
ENV MYSQL_ROOT_PASSWORD computercombat
EXPOSE 3036
