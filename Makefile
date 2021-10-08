.PHONY: build

build: build-adapter build-tests build-nginx

build-run: build run-all

build-nginx:
	sh -c 'docker build -t local/111-nginx -f Dockerfile.nginx .'

build-adapter:
	sh -c 'docker build -t local/111-adapter -f Dockerfile.111 .'

build-tests:
	sh -c 'docker build -t local/111-tests -f Dockerfile.tests .'

build-crl:
	sh -c 'cp nginx/certs/nhs_certs/test_111_3.crl nginx/crl_server_content/revocation_list.crl && docker build -t local/111-crl -f Dockerfile.crl . && rm nginx/crl_server_content/revocation_list.crl'

run-all:
	sh -c '. ./nginx/scripts/export_cert_values.sh && docker-compose -f docker-compose.yml up -d nginx'

stop-all:
	sh -c 'docker stop $$(docker ps -a -q)'

status:
	sh -c 'docker ps -a'

clean-all:
	sh -c 'docker rm -f -v $$(docker ps -a -q)'

reset: stop-all build run-all status

restart: stop-all run-all status

run-curl:
	sh -c 'curl --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443 --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl2:
	sh -c 'curl -v --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443 --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl3:
	sh -c 'curl -v --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443/auth_test --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl4:
	sh -c 'curl --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer --request POST https://integration-adaptor-111.local:8443/report --resolve integration-adaptor-111.local:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl5:
	sh -c 'curl --cacert nginx/certs/ca.cer --request POST https://integration-adaptor-111.local:8443/report --resolve integration-adaptor-111.local:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/ITK_Report_request.xml" -H "Content-Type: application/xml"'

run-curl6:
	sh -c 'curl --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer --request POST https://test02.oneoneone.nhs.uk:8443/report --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl61:
	sh -c 'curl --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer --request POST https://test02.oneoneone.nhs.uk:8443/auth_test --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl7:
	sh -c 'curl --cert nginx/certs/nhs_certs/test05.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test05_cert_chain.txt --request POST https://test02.oneoneone.nhs.uk:8443/report --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl71:
	sh -c 'curl --cert nginx/certs/nhs_certs/test05.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test05_cert_chain.txt --request POST https://test05.opentest.hscic.gov.uk:8443/report --resolve test05.opentest.hscic.gov.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl72:
	sh -c 'curl -k --cert nginx/certs/nhs_certs/test05.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test05_cert_chain.txt --request POST https://test05.opentest.hscic.gov.uk:8443/auth_test --resolve test05.opentest.hscic.gov.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl73:
	sh -c 'curl -k --cert nginx/certs/nhs_certs/test05.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test05_cert_chain.txt --request POST https://test05.opentest.hscic.gov.uk:8443/report --resolve test05.opentest.hscic.gov.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/primary-emergency-itk-request.xml" -H "Content-Type: application/xml"'

run-curl8:
	sh -c 'curl --cert nginx/certs/nhs_certs/test05.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test05_cert_chain.txt https://test02.oneoneone.nhs.uk:8443/auth_test --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1'

run-curl9:
	sh -c 'curl --cert nginx/certs/nhs_certs/test03.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test03_cert_chain.txt https://test03.oneoneone.nhs.uk:8443/report --resolve test03.oneoneone.nhs.uk:8443:127.0.0.1'

run-curl91:
	sh -c 'curl -k --pass password --cacert nginx/certs/nhs_certs/test03_cert_chain.txt https://test03.oneoneone.nhs.uk:8443/auth_test --resolve test03.oneoneone.nhs.uk:8443:127.0.0.1'

# checking CRL

run-curl92:
	sh -c 'curl --cert nginx/certs/nhs_certs/test04.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test04_cert_chain.txt https://test04.oneoneone.nhs.uk:8443/auth_test --resolve test04.oneoneone.nhs.uk:8443:127.0.0.1'

run-curl93:
	sh -c 'curl --cert nginx/certs/nhs_certs/test02.pkcs12 --cert-type p12 --pass password --cacert nginx/certs/nhs_certs/test02_cert_chain.txt https://test04.oneoneone.nhs.uk:8443/auth_test --resolve test04.oneoneone.nhs.uk:8443:127.0.0.1'


# CRL
# View: openssl crl -inform PEM -text -noout -in test_111.crl
# Check cert Serial number: openssl x509 -in test04.oneoneone.nhs.uk.crt -serial -noout
# Extract pkcs12: openssl pkcs12 -in test04.pkcs12 -nocerts -nodes -out test04_pkcs12/test04_client.key |  openssl pkcs12 -in test04.pkcs12 -nokeys -clcerts -out test04_pkcs12/test04_client.crt

#Revoked Certificates:
#    Serial Number: C1034740E0FD3816
#        Revocation Date: Feb 27 15:35:48 2020 GMT
#    Serial Number: C1034740E0FD3818
#        Revocation Date: Mar  3 13:26:17 2020 GMT