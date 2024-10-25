FROM php:7.4.33-apache

RUN docker-php-source extract \
	&& docker-php-ext-install mysqli \
	&& docker-php-source delete

# Java is necessery for the direct calls to the evaluation engine (in Api_splits.php)
RUN apt update \
    && apt install -y --no-install-recommends openjdk-11-jre 

COPY . /var/www/openml
RUN mv /var/www/openml/openml_OS/config/BASE_CONFIG-BLANK.php /var/www/openml/openml_OS/config/BASE_CONFIG.php

RUN mkdir -p /var/www/openml/logs
RUN mkdir /data


COPY docker/config/*.load /etc/apache2/mods-enabled/
COPY docker/config/api.conf /etc/apache2/sites-enabled/000-default.conf
COPY docker/config/php.ini /usr/local/etc/php/
COPY docker/config/.htaccess /var/www/openml/.htaccess

COPY docker/set_configuration.sh /scripts/


WORKDIR /var/www/openml
ENTRYPOINT ["bash", "/scripts/set_configuration.sh"]
