#!/usr/bin/env bash

rm -r -f /opt/bitnami
mkdir /opt/bitnami
mkdir /opt/bitnami/mariadb_data
mkdir /opt/bitnami/suitecrm_data

mkdir /etc/nginx
mkdir /etc/nginx/certs
mkdir /etc/nginx/vhost.d
mkdir /usr/share/nginx
mkdir /usr/share/nginx/html

#voir https://techoverflow.net/2018/12/15/solving-bitnam-docker-redmine-cannot-create-directory-bitnami-mariadb-permission-denied/
sudo chown -R 1001:1001 /opt/bitnami/mariadb_data
sudo chown -R 1001:1001 /opt/bitnami/suitecrm_data

#Installation de suitecrm
cd /root
rm -f docker-compose.yml && wget https://raw.githubusercontent.com/f80dev/myRousseauAutoServer/master/crm/docker-compose.yml
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi $(docker images -q)
docker-compose up


#fabrication des certificats
cd /opt/bitnami/suitecrm_data/apache/conf/bitnami/certs
certbot certonly --standalone -d server.f80.fr -d www.server.f80.fr
openssl x509 -outform der -in /etc/letsencrypt/live/server.f80.fr/cert.pem -out server.crt
openssl rsa -outform der  -in /etc/letsencrypt/live/server.f80.fr/privkey.pem -out server.key

reboot

#ouvrir le browser sur http://<adresse du serveur>/index.php?module=Home&action=index
