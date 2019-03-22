#!/usr/bin/env bash

cd /root
mkdir blog
cd blog
wget https://raw.githubusercontent.com/f80dev/myRousseauAutoServer/master/Services%20annexes/blog/docker-compose.yml
docker-compose up

