<?php
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  After editing this file, rename to BASE_CONFIG.php
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Sets the local time to a specific zone. Can be removed, if it is
 * set in php.ini.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
setlocale(LC_TIME, 'nl_NL');

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BASE_URL: The domain of the instance, along with the folder where
 * it can be found. Also with a tailing slash. For the main instance
 * of OpenML, this would be http://www.openml.org/. For an instance on
 * a localhost, this would be http://localhost/.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'BASE_URL', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DATA_URL: The subdirectory where the data can be accessed. Will be
 * deprecated in a few updates.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DATA_URL', BASE_URL . 'data/' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * MINIO_URL: MINIO Server URL. Currently this is 'https://openml1.win.tue.nl/'.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'MINIO_URL', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * PATH: The directory on the hard disk where the instance of OpenML
 * can be found, with tailing slash. Typically, this would be
 * something like /var/www/ (on Ubuntu installations)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'PATH', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DATA_PATH: The directory on whether the uploaded data is stored.
 * It might be a good idea to locate this directory outside of the
 * webserver directory. Should be writeable for webserver.
 * IMPORTANT NOTE: If you choose to change the value indicated below
 * (PATH . 'data/') into something different, make sure to also move
 * the content of the data directory that was provided at installation.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DATA_PATH', PATH . 'data/' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * TMP_PATH: Directory in which temporary files can be places. LOCK
 * files are an example of these kind of files. /tmp/ is generally a
 * good place on Unix based systems.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'TMP_PATH', '/tmp/' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Paths for server-side scripts
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'LIB_PATH', '/var/lib/' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Configuration details for the experiment database
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_NAME: The name of the database
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_HOST: The host of the database (typically: localhost)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_USER: The username to connect to the database. IMPORTANT: This
 * account is used by the free query interface. This username should
 * only contain READ (SELECT, DESCRIBE) access to the database
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_PASS: The password that belongs to the username
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_USER_WRITING: A user account that can also WRITE (SELECT,
 * UPDATE, INSERT, DELETE) to the database. Administrative privileges
 * are discouraged.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_PASS_WRITING: The password that belongs to the DB_USER_WRITING
 * account.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DB_NAME_EXPDB', 'FILL_IN' );
define( 'DB_HOST_EXPDB', 'FILL_IN' );
define( 'DB_USER_EXPDB_READ', 'FILL_IN' );
define( 'DB_PASS_EXPDB_READ', 'FILL_IN' );
define( 'DB_USER_EXPDB_WRITE', 'FILL_IN' );
define( 'DB_PASS_EXPDB_WRITE', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Configuration details for the OpenML database (User accounts, etc.)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_NAME_COMMUNITY: The name of the database
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_HOST_COMMUNITY: The host of the database (typically: localhost)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_USER_COMMUNITY: The username to connect to the database. Should
 * have SELECT, INSERT, UPDATE and DELETE privileges.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DB_PASS: The password that belongs to the username
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DB_NAME_OPENML', 'FILL_IN' );
define( 'DB_HOST_OPENML', 'FILL_IN' );
define( 'DB_USER_OPENML', 'FILL_IN' );
define( 'DB_PASS_OPENML', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Configuration details for the OpenML API (Username, password)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * API_USERNAME: The username under which the system may perform API
 * calls.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * API_KEY: The API_KEY that belongs to the username
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'API_USERNAME', 'FILL_IN_USERNAME' );
define( 'API_KEY', 'FILL_IN_KEY' );


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Configuration details for the ElasticSearch server (Username, password)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ES_USERNAME and ES_PASSWORD can be set by ElasticSearch or a protected proxy
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'ES_URL', 'FILL_IN' );
define( 'ES_USERNAME', 'FILL_IN' );
define( 'ES_PASSWORD', 'FILL_IN' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Configuration details for the Wiki server (Username, password)
 * We use the Gollem Wiki Engine
 * WIKI_USERNAME and WIKI_PASSWORD can be set by a protected proxy
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'WIKI_URL', 'localhost:443/wiki' );
define( 'WIKI_USERNAME', 'username' );
define( 'WIKI_PASSWORD', 'password' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * *
 * DEBUG: Will produce errors and warnings on the screen. Set this
 * to true when developing. Set to false on the production server
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DEBUG', FALSE );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * *
 * DEBUG_XSD_EMAIL: Will send debug information for failed XSD
 * validations to the user who initiated it
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'DEBUG_XSD_EMAIL', FALSE );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * CMD_PREFIX: Adds a prefix to all shell commands; needed for bugfix
 * on some MAMP (OSX) systems. Can be left empty on all other systems.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'CMD_PREFIX', '');

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EMAIL_FROM: The email address from which the emails will be send.
 * Use the domain name specified in the BASE_URL
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'EMAIL_FROM', 'noreply@openml.org' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EMAIL_API_LOG: The email address to which critical API errors
 * get reported
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'EMAIL_API_LOG', 'api@openml.org' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL API SETTINGS
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL_API_PROXY: Set this to true if you want to use a proxy
 * for communication with external proxies. In general, you don't
 * need this (and can skip this block by setting this to false).
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL_API_PROXY_PORT: The port of the proxy
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL_API_PROXY_URL: The URL of the proxy
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL_API_PASSWORD: The password of the proxy
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'EXTERNAL_API_PROXY', FALSE );
define( 'EXTERNAL_API_PROXY_PORT', '' );
define( 'EXTERNAL_API_PROXY_URL', '' );
define( 'EXTERNAL_API_PASSWORD', '' );

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EXTERNAL API KEYS - The public and private keys of the external api's
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Typically, an external api provides us with a public keyb
 * (FACEBOOK_APP_ID, TWITTER_CONSUMER_KEY, GOOGLE_CLIENT_ID) and a
 * private key (FACEBOOK_APP_SECRET, TWITTER_CONSUMER_SECRET,
 * GOOGLE_CLIENT_SECRET). These are to be filled in here.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
define( 'FACEBOOK_APP_ID', 'xxx' );
define( 'FACEBOOK_APP_SECRET', 'xxx' );
define( 'TWITTER_CONSUMER_KEY', 'xxx' );
define( 'TWITTER_CONSUMER_SECRET', 'xxx' );
define( 'GOOGLE_CLIENT_ID', 'xxx.apps.googleusercontent.com' );
define( 'GOOGLE_CLIENT_SECRET', 'xxx' );
?>
