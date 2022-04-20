FROM nginx:1.21.6

RUN apt-get update && apt-get install -y ruby wget
COPY nginx/conf/default-ssl.conf.erb /etc/nginx/conf.d/default-ssl.conf.erb

COPY nginx/scripts/30-copy_certs.sh /docker-entrypoint.d
COPY nginx/scripts/35-prepare_config.sh /docker-entrypoint.d
COPY nginx/scripts/40-debug.sh /docker-entrypoint.d

EXPOSE 80
EXPOSE 443