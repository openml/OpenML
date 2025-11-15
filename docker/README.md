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
