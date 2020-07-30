#!/bin/bash
# remove the original config from nginx dir
rm -f /etc/nginx/conf.d/default.conf
erb /etc/nginx/conf.d/default-ssl.conf.erb > /etc/nginx/conf.d/default-ssl.conf