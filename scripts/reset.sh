#!/bin/bash

# Get info
read -p 'MYSQL Username: ' uservar
read -sp 'MYSQL Password: ' passvar

# Variables
OPENML_DIR=$PWD                   # Default is current directory
OPENML_DATA="$PWD/data"           # Set to data directory
OPENML_LOG="$OPENML_DATA/log"
MYSQL_USER=$uservar             # MySQL user name and password
MYSQL_PASS=$passvar

# delete data from server
echo "Deleting server data... "
sudo rm -rf $OPENML_LOG

# delete database
echo "Deleting databases... "
mysqladmin -u $MYSQL_USER -p$MYSQL_PASS DROP openml_expdb
mysqladmin -u $MYSQL_USER -p$MYSQL_PASS DROP openml

# delete search indices
echo "Deleting search indices... "
curl -XDELETE http://localhost:9200/_all

chmod a+x ./scripts/install.sh
sh ./scripts/install.sh
