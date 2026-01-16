FROM php:7.4.33-apache

RUN docker-php-source extract \
	&& docker-php-ext-install mysqli \
	&& docker-php-source delete

# Java is necessary for the direct calls to the evaluation engine (in Api_splits.php)
# cron is needed for garbage collection
RUN apt update \
    && apt install -y --no-install-recommends openjdk-11-jre \
    && apt install -y cron

COPY . /var/www/openml
RUN mv /var/www/openml/openml_OS/config/BASE_CONFIG-BLANK.php /var/www/openml/openml_OS/config/BASE_CONFIG.php

RUN mkdir -p /var/www/openml/logs && \
    mkdir -p /var/www/data/log && \
    mkdir /data && \
    chown -R www-data:www-data /var/www/data

COPY docker/config/*.load /etc/apache2/mods-enabled/
COPY docker/config/api.conf /etc/apache2/sites-enabled/000-default.conf
COPY docker/config/php.ini /usr/local/etc/php/
COPY docker/config/.htaccess /var/www/openml/.htaccess

COPY docker/garbage-collection/cron /etc/cron.d/garbage-collection
RUN chmod +x /etc/cron.d/garbage-collection
RUN crontab /etc/cron.d/garbage-collection
RUN touch /var/log/garbage-collection.log
RUN chmod +x /var/www/openml/docker/garbage-collection/sessionclean.sh

COPY docker/set_configuration.sh /scripts/


WORKDIR /var/www/openml
ENTRYPOINT ["bash", "/scripts/set_configuration.sh"]
