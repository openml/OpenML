# Running apache php backend locally

In most cases, you probably want to run through docker compose.
This file contains instructions for running it on its own.

```bash
docker run -p 8001:80 --rm -it openml/php-rest-api
```

Runs the PHP REST API server and exposes it to `http://localhost:8001/`.
Some `BASE_CONFIG.php` variables can be overwritten with environment variables,
these can be passed in the run command with the `-e` option, e.g.: `-e BASE_URL=http://localhost/`.
See `set_configuration.sh` for the variables which can be overwritten out-of-the-box.
Alternatively, mount your own `BASE_CONFIG.php` into the container at `/var/www/openml/openml_OS/config/BASE_CONFIG.php`.
The `set_configuration.sh` script will only overwrite unset variables.
To avoid overwriting altogether, also change the entrypoint: `--entrypoint=apache2-foreground`.

To connect to a separate container running a MySQL server, they need to be on the same docker network.
For both, specify the network with `--network NETWORK_NAME`, which can be any network you create with `docker network create NETWORK_NAME`.
Assuming a connection to the database can be established, to get a dataset description go to `http://127.0.0.1:8001/api/v1/json/data/1`.
Note that the protocol is `http` not `https`.


## Build

```bash
docker build --tag openml/php-rest-api -f docker/Dockerfile .
```

## Configuration
The following environment variables can be set (see also `set_configuration.sh`):

| NAME | DEFAULT | FILE | NOTE |
|------|---------|------|------|
| `BASE_URL` | `https://test.openml.org/` | BASE_CONFIG.php | sets `BASE_URL` |
| `MINIO_URL` | `https://openml1.win.tue.nl/` | BASE_CONFIG.php | sets `MINIO_URL` |
| `OPENML_PATH` | `/var/www/` | BASE_CONFIG.php | sets `PATH` |
| `API_KEY` | `FILL_IN_KEY` | BASE_CONFIG.php | sets `API_KEY` |
| `DB_NAME_EXPDB` | `openml_expdb` | BASE_CONFIG.php | sets `DB_NAME_EXPDB` |
| `DB_HOST_EXPDB` | `openml-test-database:3306` | BASE_CONFIG.php | sets `DB_HOST_EXPDB` |
| `DB_USER_EXPDB_READ` | `root` | BASE_CONFIG.php | sets `DB_USER_EXPDB_READ` |
| `DB_PASS_EXPDB_READ` | `ok` | BASE_CONFIG.php | sets `DB_PASS_EXPDB_READ` |
| `DB_USER_EXPDB_WRITE` | `root` | BASE_CONFIG.php | sets `DB_USER_EXPDB_WRITE` |
| `DB_PASS_EXPDB_WRITE` | `ok` | BASE_CONFIG.php | sets `DB_PASS_EXPDB_WRITE` |
| `DB_NAME_OPENML` | `openml` | BASE_CONFIG.php | sets `DB_NAME_OPENML` |
| `DB_HOST_OPENML` | `openml-test-database:3306` | BASE_CONFIG.php | sets `DB_HOST_OPENML` |
| `DB_USER_OPENML` | `root` | BASE_CONFIG.php | sets `DB_USER_OPENML` |
| `DB_PASS_OPENML` | `ok` | BASE_CONFIG.php | sets `DB_PASS_OPENML` |
| `ES_URL` | `elasticsearch:9200` | BASE_CONFIG.php | sets `ES_URL` |
| `ES_USERNAME` | `elastic` | BASE_CONFIG.php | sets `ES_USERNAME` |
| `ES_PASSWORD` | `default` | BASE_CONFIG.php | sets `ES_PASSWORD` |
| `PHP_ENVIRONMENT` | `production` | index.php | sets the `ENVIRONMENT` constant |
| `MYSQLND_NET_READ_TIMEOUT` | `600` | php.ini | sets `mysqlnd.net_read_timeout` |
| `INDEX_ES_DURING_STARTUP` | `true` | — | if `true`, deletes and rebuilds all Elasticsearch indices on startup |

For information on what these variables do, reference the documentation of the respective files.
