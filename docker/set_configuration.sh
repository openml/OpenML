#!/bin/bash

# TODO: read credentials from secrets instead
OPENML_PATH=${OPENML_PATH:-/var/www/}
BASE_CONFIG_PATH=${OPENML_PATH}openml/openml_OS/config/BASE_CONFIG.php
INDEX_PATH=${OPENML_PATH}openml/index.php

# We expect some paths/urls to contain '/' characters, so we use '*' instead
sed "s*'BASE_URL', 'FILL_IN'*'BASE_URL', '${BASE_URL:-https://test.openml.org/}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'MINIO_URL', 'FILL_IN'*'MINIO_URL', '${MINIO_URL:-https://openml1.win.tue.nl/}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'PATH', 'FILL_IN'*'PATH', '${OPENML_PATH:-/var/www/}'*g" --in-place ${BASE_CONFIG_PATH}

sed "s*'API_KEY', 'FILL_IN_KEY'*'API_KEY', '${API_KEY:-FILL_IN_KEY}'*g" --in-place ${BASE_CONFIG_PATH}

sed "s*'DB_NAME_EXPDB', 'FILL_IN'*'DB_NAME_EXPDB', '${DB_NAME_EXPDB:-openml_expdb}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_HOST_EXPDB', 'FILL_IN'*'DB_HOST_EXPDB', '${DB_HOST_EXPDB:-openml-test-database:3306}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_USER_EXPDB_READ', 'FILL_IN'*'DB_USER_EXPDB_READ', '${DB_USER_EXPDB_READ:-root}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_PASS_EXPDB_READ', 'FILL_IN'*'DB_PASS_EXPDB_READ', '${DB_PASS_EXPDB_READ:-ok}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_USER_EXPDB_WRITE', 'FILL_IN'*'DB_USER_EXPDB_WRITE', '${DB_USER_EXPDB_WRITE:-root}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_PASS_EXPDB_WRITE', 'FILL_IN'*'DB_PASS_EXPDB_WRITE', '${DB_PASS_EXPDB_WRITE:-ok}'*g" --in-place ${BASE_CONFIG_PATH}

sed "s*'DB_NAME_OPENML', 'FILL_IN'*'DB_NAME_OPENML', '${DB_NAME_OPENML:-openml}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_HOST_OPENML', 'FILL_IN'*'DB_HOST_OPENML', '${DB_HOST_OPENML:-openml-test-database:3306}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_USER_OPENML', 'FILL_IN'*'DB_USER_OPENML', '${DB_USER_OPENML:-root}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'DB_PASS_OPENML', 'FILL_IN'*'DB_PASS_OPENML', '${DB_PASS_OPENML:-ok}'*g" --in-place ${BASE_CONFIG_PATH}

sed "s*'ES_URL', 'FILL_IN'*'ES_URL', '${ES_URL:-elasticsearch:9200}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'ES_USERNAME', 'FILL_IN'*'ES_USERNAME', '${ES_USERNAME:-elastic}'*g" --in-place ${BASE_CONFIG_PATH}
sed "s*'ES_PASSWORD', 'FILL_IN'*'ES_PASSWORD', '${ES_PASSWORD:-default}'*g" --in-place ${BASE_CONFIG_PATH}

sed "s/define('ENVIRONMENT', '.*')/define('ENVIRONMENT', '${PHP_ENVIRONMENT:-production}')/" --in-place ${INDEX_PATH}


indices=('downvote', 'study', 'data', 'task', 'download', 'user', 'like', 'measure', 'flow', 'task_type', 'run')
for index in "${indices[@]}"
do
	curl -X DELETE ${ES_URL:-elasticsearch:9200}/${index}?ignore_unavailable=true
done

cd /var/www/openml
php index.php cron build_es_indices

apache2-foreground
