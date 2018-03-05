### Docker installation
The easiest way to set up a local version is to use the docker container provided here:
https://github.com/nodechef/openML_docker_server

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