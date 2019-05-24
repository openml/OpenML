#!/bin/bash

# Variables
OPENML_DIR=$PWD                   # Default is current directory
OPENML_DATA="$PWD/data"           # Set to data directory
OPENML_LOG="$OPENML_DATA/log"
MYSQL_USER="youruser"             # MySQL user name and password
MYSQL_PASS="yourpassword"

# delete data from server
echo "Deleting server data... "
sudo rm -rf $OPENML_DATA

# delete database
echo "Deleting databases... "
mysqladmin -u $MYSQL_USER -p $MYSQL_PASS DROP openml_expdb
mysqladmin -u $MYSQL_USER -p $MYSQL_PASS DROP openml

# delete search indices
echo "Deleting search indices... "
curl -XDELETE http://localhost:9200/_all

chmod a+x ./install.sh
sh ./install.sh
