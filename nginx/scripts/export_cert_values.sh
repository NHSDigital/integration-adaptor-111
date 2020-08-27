#!/bin/bash

# This scripts reads the values of files with certs and exports them as env variables
# to be used for local development, run from the top directory of repo

export NGINX_PUBLIC_CERT="$(cat nginx/certs/server_public.crt)"
export NGINX_PRIVATE_CERT="$(cat nginx/certs/server_private.key)"
export NGINX_CLIENT_PUBLIC_CERT="$(cat nginx/certs/client_public.crt)"
export NGINX_CA_CERT="$(cat nginx/certs/ca.cer)"
export NGINX_CRL="$(cat nginx/certs/revocation_list.crl)"
