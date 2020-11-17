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

echo "Creating directories... "
#mkdir $OPENML_DATA
mkdir $OPENML_LOG
sudo chown apache:apache $OPENML_LOG
sudo chown apache:apache "$OPENML_LOG/*"

# go to right directory
cd $OPENML_DIR

# reinitiate the database
echo "Building databases... "
mysql -u $MYSQL_USER -p$MYSQL_PASS < downloads/openml.sql
mysql -u $MYSQL_USER -p$MYSQL_PASS openml < /home/jvanscho/users-secret.sql
mysql -u $MYSQL_USER -p$MYSQL_PASS < downloads/openml_expdb.sql

# fill important columns
sudo php index.php cron install_database

# create ES search index
echo "Building search indices... "
sudo php index.php cron build_es_indices
