#!/usr/bin/env bash

mkdir /home/mariadb_data
mkdir /home/suitecrm_data

#voir https://techoverflow.net/2018/12/15/solving-bitnami-docker-redmine-cannot-create-directory-bitnami-mariadb-permission-denied/
sudo chown -R 1001:1001 /home/mariadb_data
sudo chown -R 1001:1001 /home/suitecrm_data

wget https://raw.githubusercontent.com/f80dev/myRousseauAutoServer/master/crm/docker-compose.yml
docker-compose up -d

#ouvrir le browser sur http://<adresse du serveur>/index.php?module=Home&action=index
