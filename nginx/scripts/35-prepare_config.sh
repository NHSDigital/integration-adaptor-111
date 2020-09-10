#!/bin/bash
# remove the original config from nginx dir
rm -f /etc/nginx/conf.d/default.conf
# Get the CN from certificate file:
CERTS_DIR=/etc/ssl
SERVER_SSL_CN=`openssl x509 -noout -in ${CERTS_DIR}/server_public.crt -subject | awk -F= '{print $NF}' | sed -e 's/^[ \t]*//'`
# run Ruby ERB template on nginx config
SERVER_SSL_CN=${SERVER_SSL_CN} erb /etc/nginx/conf.d/default-ssl.conf.erb > /etc/nginx/conf.d/default-ssl.conf
