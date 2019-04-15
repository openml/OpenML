#!/bin/bash

# Variables
OPENML_DIR=$PWD                   # Default is current directory
OPENML_DATA="$PWD/data"           # Set to data directory
OPENML_LOG="$OPENML_DATA/log"
MYSQL_USER="youruser"             # MySQL user name and password
MYSQL_PASS="yourpassword"

echo "Creating directories... "
mkdir $OPENML_DATA
mkdir $OPENML_LOG
sudo chown apache:apache $OPENML_LOG
sudo chown apache:apache "$OPENML_LOG/*"

# go to right directory
cd $OPENML_DIR

# reinitiate the database
echo "Building databases... "
mysql -u $MYSQL_USER -p $MYSQL_PASS < downloads/openml_expdb.sql
mysql -u $MYSQL_USER -p $MYSQL_PASS < downloads/openml.sql

# fill important columns
sudo php index.php cron install_database

# create ES search index
echo "Building search indices... "
curl -XPUT 'localhost:9200/data' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/run' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/flow' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/task' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/user' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/measure' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/task_type' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/study' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/like' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/download' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'
curl -XPUT 'localhost:9200/downvote' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 1,
            "number_of_replicas" : 0
        }
    }
}
'

sudo php index.php cron build_es_indices
