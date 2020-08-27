#!/bin/bash

# Used for debugging the nginx container
# Outputs to stdout (logs) the contents of nginx config and cert files

SHOW_ENV_VARS="false"
if [ ${SHOW_ENV_VARS} == "true" ]
then
echo "Public server:"
echo ${NGINX_PUBLIC_CERT}
echo "Private cert:"
echo ${NGINX_PRIVATE_CERT}
echo "client public cert:"
echo ${NGINX_CLIENT_PUBLIC_CERT}
echo "CA cer"
echo ${NGINX_CA_CERT}
fi

if [ ${DEBUG} == "true" ]
then
  echo "Main Nginx config"
  cat /etc/nginx/nginx.conf
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo "Site configuration"
  cat /etc/nginx/conf.d/default-ssl.conf
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo "CA cert"
  cat /etc/ssl/ca.crt
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo "Server public cert"
  cat /etc/ssl/server_public.crt
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo "Server private key"
  cat /etc/ssl/server_private.key
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo "Client public cert"
  cat /etc/ssl/client_public.crt
  echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
fi
