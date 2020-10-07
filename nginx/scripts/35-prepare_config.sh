#!/bin/bash
# remove the original config from nginx dir
rm -f /etc/nginx/conf.d/default.conf
# Get the CN from certificate file:
CERTS_DIR=/etc/ssl
SERVER_SSL_CN=`openssl x509 -noout -in ${CERTS_DIR}/server_public.crt -subject | awk -F= '{print $NF}' | sed -e 's/^[ \t]*//'`

# Check if the server ssl has expired - if so, the page will always return 403
SERVER_SSL_VALIDITY_EPOCH=`date -d "$(openssl x509 -in ${CERTS_DIR}/server_public.crt -noout -enddate | sed 's/notAfter=//g')" +"%s"`
SERVER_SSL_EXPIRED="false"
if [ ${SERVER_SSL_VALIDITY_EPOCH} -gt $(date -d now +%"s") ]
then
  SERVER_SSL_EXPIRED="false"
else
  SERVER_SSL_EXPIRED="true"
fi

# run Ruby ERB template on nginx config
SERVER_SSL_CN=${SERVER_SSL_CN} SERVER_SSL_EXPIRED=${SERVER_SSL_EXPIRED} erb /etc/nginx/conf.d/default-ssl.conf.erb > /etc/nginx/conf.d/default-ssl.conf
