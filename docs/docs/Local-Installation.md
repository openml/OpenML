### Docker installation
The easiest way to set up a local version of OpenML is to use Docker Compose following the instructions here (thanks to Rui Quintino!):
https://github.com/openml/openml-docker-dev

If you run into problems, please post an issue in the same github repo.

### Requirements
You'll need to have the following software running:
* Apache Webserver, (with the rewrite module enabled. Is installed by default,
not enabled.)
* MySQL Server.
* PHP 5.5 or higher (comes also with Apache)
Or just a XAMP (Mac), LAMP (Linux) or WAMP (Windows) package, which conveniently contains all these applications.

### Databases
Next, OpenML runs on two databases, a public database with all experiment information, and a private database, with information like user accounts etc. The latest version of both databases can be downloaded here: https://www.openml.org/guide/developers

Obviously, the private database does not include any actual user account info.

### Backend
The source code is available in the 'OpenML' repository: https://github.com/openml/OpenML

OpenML is written in PHP, and can be 'installed' by copying all files in the 'www' or 'public_html' directory of Apache.

After that, you need to provide your local paths and database accounts and passwords using the config file in:
'APACHE_WWW_DIR'/openml_OS/config/BASE_CONFIG.php.

If everything is configured correctly, OpenML should now be running.

### Search Indices
If you want to run your own (separate) OpenML instance, and store your own data, you'll also want to build your own search indices to show all data on the website. The OpenML website is based on the ElasticSearch stack. To install it, follow the instructions here: http://knowm.org/how-to-set-up-the-elk-stack-elasticsearch-logstash-and-kibana/

### Initialization
This script wipes all OpenML server data and rebuilds the database and search index. Replace 'openmldir' with the directory where you want OpenML to store files.

```
# delete data from server
sudo rm -rf /openmldir/*
mkdir /openmldir/log

# delete database
mysqladmin -u "root" -p"yourpassword" DROP openml_expdb
mysql -h localhost -u root -p"yourpassword" -e "TRUNCATE openml.file;"

# reset ES search index
echo "Deleting and recreating the ES index: "
curl -XDELETE http://localhost:9200/openml
curl -XPUT 'localhost:9200/openml?pretty' -H 'Content-Type: application/json' -d'
{
    "settings" : {
        "index" : {
            "number_of_shards" : 3,
            "number_of_replicas" : 2
        }
    }
}
'

# go to directory with the website source code
cd /var/www/openml.org/public_html/

# reinitiate the database
mysql -u root -p"yourpassword!" < downloads/openml_expdb.sql

# fill important columns
sudo php index.php cron install_database

# rebuild search index
sudo php index.php cron initialize_es_indices
sudo php index.php cron build_es_indices

sudo chown apache:apache /openmldir/log
sudo chown apache:apache /openmldir/log/*
```
