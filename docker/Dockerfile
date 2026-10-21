FROM eclipse-temurin:11-jre AS builder

FROM php:8.5.11-apache

RUN docker-php-source extract \
	&& docker-php-ext-install mysqli \
	&& docker-php-source delete

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=builder $JAVA_HOME $JAVA_HOME

ENV MAX_REQUEST_WORKERS=152

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
COPY docker/config/mpm_prefork.conf /etc/apache2/mods-enabled/mpm_prefork.conf

COPY docker/set_configuration.sh /scripts/


WORKDIR /var/www/openml
ENTRYPOINT ["bash", "/scripts/set_configuration.sh"]
