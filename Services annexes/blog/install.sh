#!/usr/bin/env bash

mkdir blog
cd blog
wget https://raw.githubusercontent.com/anchorcms/anchor-cms/master/docker-compose.yml
docker-compose up

