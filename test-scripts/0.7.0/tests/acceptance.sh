#!/bin/bash

set -ex

#adaptor only
curl -i --location --request POST 'http://localhost:8080/report' \
     --data '@../../../service/src/test/resources/xml/ITK_Report_request.xml' \
     -H "Content-Type: application/xml"


#nginx + TLS
#prerequisite - certificates are set
#curl --cert <path_to_client_pkcs_12> --cert-type p12 --pass password --cacert <path_to_ca_cert> --request POST https://test02.oneoneone.nhs.uk:8443/report --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1 --data "@../../../service/src/integration-test/resources/xml/ITK_Report_request.xml" -H "Content-Type: application/xml"