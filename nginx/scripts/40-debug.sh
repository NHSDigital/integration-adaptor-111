#!/bin/bash

# Used for debugging the nginx container
# Outputs to stdout (logs) the contents of nginx config and cert files

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
