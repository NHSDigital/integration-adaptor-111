#!/bin/bash
# This script will copy the certificates from env variables to files used by nginx

# For some reason docker-compose changes \n in certs to spaces, probably with xargs
# and nginx fails to load those certs
# we need to restore those to the original state
# in a way that it also not break a properly formatted certs
# in case AWS provides them intact

CERTS_DIR=/etc/ssl

# certs intro
echo "-----BEGIN CERTIFICATE-----" > ${CERTS_DIR}/client_public_only.crt
echo "-----BEGIN CERTIFICATE-----" > ${CERTS_DIR}/server_public.crt
echo "-----BEGIN CERTIFICATE-----" > ${CERTS_DIR}/ca.crt

# certs payload
echo ${NGINX_CLIENT_PUBLIC_CERT} | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/client_public_only.crt
echo ${NGINX_PUBLIC_CERT}        | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/server_public.crt
echo ${NGINX_CA_CERT}            | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/ca.crt

# cerst outro
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/client_public_only.crt
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/server_public.crt
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/ca.crt

# keys
echo "-----BEGIN RSA PRIVATE KEY-----" > ${CERTS_DIR}/server_private.key
echo ${NGINX_PRIVATE_CERT} | sed 's/-----BEGIN RSA PRIVATE KEY----- //' | sed 's/-----BEGIN RSA PRIVATE KEY-----//' | sed 's/ -----END RSA PRIVATE KEY-----//' | sed 's/-----END RSA PRIVATE KEY-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/server_private.key
echo "-----END RSA PRIVATE KEY-----" >> ${CERTS_DIR}/server_private.key

# combine CA with client public
cat ${CERTS_DIR}/ca.crt > ${CERTS_DIR}/client_public.crt
cat ${CERTS_DIR}/client_public_only.crt >> ${CERTS_DIR}/client_public.crt
