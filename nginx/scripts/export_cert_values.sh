#!/bin/bash

# This scripts reads the values of files with certs and exports them as env variables
# to be used for local development, run from the top directory of repo

#clear all variables
unset NGINX_PUBLIC_CERT
unset NGINX_PRIVATE_CERT
unset NGINX_CLIENT_PUBLIC_CERT
unset NGINX_CA_CERT
unset NGINX_CRL
unset NGINX_CRL_URL

export NGINX_PUBLIC_CERT="$(cat nginx/certs/server_public.cer)"
export NGINX_PRIVATE_CERT="$(cat nginx/certs/server_private.key)"
export NGINX_CLIENT_PUBLIC_CERT="$(cat nginx/certs/client_public.cer)"
export NGINX_CA_CERT="$(cat nginx/certs/ca.cer)"
#export NGINX_CRL="$(cat nginx/certs/revocation_list.crl)"
export NGINX_CRL_URL="http://crl-server:3000/revocation_list.crl"
