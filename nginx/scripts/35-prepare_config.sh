#!/bin/bash
# remove the original config from nginx dir
rm -f /etc/nginx/conf.d/default.conf
# Get the CN from certificate file:
CERTS_DIR=/etc/ssl

sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=0/g' "${CERTS_DIR}/openssl.cnf"

SERVER_SSL_CN=`openssl x509 -noout -in ${CERTS_DIR}/server_public.crt -subject | awk -F= '{print $NF}' | sed -e 's/^[ \t]*//'`

# Check if the server ssl has expired - if so, the page will always return 403
SERVER_SSL_VALIDITY_EPOCH=`date -d "$(openssl x509 -in ${CERTS_DIR}/server_public.crt -noout -enddate | sed 's/notAfter=//g')" +"%s"`
SERVER_SSL_EXPIRED=1 # assume it is expired
if [ ${SERVER_SSL_VALIDITY_EPOCH} -gt $(date -d now +%"s") ]
then
  SERVER_SSL_EXPIRED=0  # Cert has not expired
else
  SERVER_SSL_EXPIRED=1  # Cert has expired
fi

# Check if the server certificate is revoked by CRL - if so the page will always return 403
if [ -f ${CERTS_DIR}/revocation_list.crl ]
then
  openssl verify -verbose -crl_check -CAfile ${CERTS_DIR}/ca.crt -CRLfile ${CERTS_DIR}/revocation_list.crl ${CERTS_DIR}/server_public.crt > ssl_check.log 2>&1
  SERVER_SSL_REVOKED_STATUS="$?"
  cat ssl_check.log
  if [ ${SERVER_SSL_REVOKED_STATUS} = "0" ]
  then
    echo "Cert ok, status ${SERVER_SSL_REVOKED_STATUS}"
    SERVER_SSL_REVOKED=0 # Cert is not revoked
  else
    SERVER_SSL_REVOKED=1 # Cert is revoked
    echo "Cert revoked, status ${SERVER_SSL_REVOKED_STATUS}"
  fi
else
  echo "Skipping CRL check - CRL not set"
  SERVER_SSL_REVOKED=0
fi

# run Ruby ERB template on nginx config
SERVER_SSL_REVOKED=${SERVER_SSL_REVOKED} SERVER_SSL_CN=${SERVER_SSL_CN} SERVER_SSL_EXPIRED=${SERVER_SSL_EXPIRED} erb /etc/nginx/conf.d/default-ssl.conf.erb > /etc/nginx/conf.d/default-ssl.conf
