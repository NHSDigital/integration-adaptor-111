#!/bin/bash
# This script will copy the certificates from env variables to files used by nginx

# For some reason docker-compose changes \n in certs to spaces, probably with xargs
# and nginx fails to load those certs
# we need to restore those to the original state
# in a way that it also not break a properly formatted certs
# in case AWS provides them intact

CERTS_DIR=/etc/ssl
if [ ${RUN_IN_DOCKER} == "true" ]
then

# split the CA cer to files with single cert in it
echo ${NGINX_CA_CERT} > ca_temp.crt
sed -i 's/ -----BEGIN CERTIFICATE----- /-----BEGIN CERTIFICATE-----\n/g' ca_temp.crt
sed -i 's/-----BEGIN CERTIFICATE----- /-----BEGIN CERTIFICATE-----\n/g' ca_temp.crt
sed -i 's/ -----END CERTIFICATE-----/-----END CERTIFICATE-----\n/g' ca_temp.crt
sed -i 's/-----END CERTIFICATE-----/\n-----END CERTIFICATE-----/g' ca_temp.crt
csplit --prefix ca.split_ -z ca_temp.crt '/^-----BEGIN CERTIFICATE-----$/' '{*}'

# fix all the resultant certs
for CERT_FILE in $(ls ca.split_*)
do
echo "-----BEGIN CERTIFICATE-----" >> ${CERTS_DIR}/ca_temp2.crt
cat ${CERT_FILE} | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/ca_temp2.crt
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/ca_temp2.crt
done
cat ${CERTS_DIR}/ca_temp2.crt | grep . > ${CERTS_DIR}/ca.crt # remove empty lines
rm ${CERTS_DIR}/ca_temp2.crt
rm ca.split_*

# certs intro
echo "-----BEGIN CERTIFICATE-----" > ${CERTS_DIR}/client_public_only.crt
echo "-----BEGIN CERTIFICATE-----" > ${CERTS_DIR}/server_public.crt

# certs payload
echo ${NGINX_CLIENT_PUBLIC_CERT} | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/client_public_only.crt
echo ${NGINX_PUBLIC_CERT}        | sed 's/-----BEGIN CERTIFICATE----- //' | sed 's/-----BEGIN CERTIFICATE-----//' | sed 's/ -----END CERTIFICATE-----//' | sed 's/-----END CERTIFICATE-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/server_public.crt

# cerst outro
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/client_public_only.crt
echo "-----END CERTIFICATE-----" >> ${CERTS_DIR}/server_public.crt

# keys
echo "-----BEGIN RSA PRIVATE KEY-----" > ${CERTS_DIR}/server_private.key
echo ${NGINX_PRIVATE_CERT} | sed 's/-----BEGIN RSA PRIVATE KEY----- //' | sed 's/-----BEGIN RSA PRIVATE KEY-----//' | sed 's/ -----END RSA PRIVATE KEY-----//' | sed 's/-----END RSA PRIVATE KEY-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/server_private.key
echo "-----END RSA PRIVATE KEY-----" >> ${CERTS_DIR}/server_private.key

# crl
# echo "-----BEGIN X509 CRL-----" > ${CERTS_DIR}/revocation_list.crl
# echo ${NGINX_CRL} | sed 's/-----BEGIN X509 CRL----- //' | sed 's/-----BEGIN X509 CRL-----//' | sed 's/ -----END X509 CRL-----//' | sed 's/-----END X509 CRL-----//' | sed 's/ /\n/g' >> ${CERTS_DIR}/revocation_list.crl
# echo "-----END X509 CRL-----" >> ${CERTS_DIR}/revocation_list.crl


# combine CA with client public
cat ${CERTS_DIR}/ca.crt > ${CERTS_DIR}/client_public.crt
cat ${CERTS_DIR}/client_public_only.crt >> ${CERTS_DIR}/client_public.crt
fi
