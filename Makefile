.PHONY: build

build: build-adapter build-tests build-nginx

build-run: build run-all

build-nginx:
	sh -c 'docker build -t local/111-nginx -f Dockerfile.nginx .'

build-adapter:
	sh -c 'docker build -t local/111-adapter -f Dockerfile.111 .'

build-tests:
	sh -c 'docker build -t local/111-tests -f Dockerfile.tests .'

run-all:
	sh -c '. ./nginx/scripts/export_cert_values.sh && docker-compose -f docker-compose.yml up -d nginx'

stop-all:
	sh -c 'docker stop $$(docker ps -a -q)'

status:
	sh -c 'docker ps -a'

clean-all:
	sh -c 'docker rm -f -v $$(docker ps -a -q)'

run-curl:
	sh -c 'curl --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443 --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl2:
	sh -c 'curl -v --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443 --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl3:
	sh -c 'curl -v --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer https://integration-adaptor-111.local:8443/auth_test --resolve integration-adaptor-111.local:8443:127.0.0.1'

run-curl4:
	sh -c 'curl --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer --request POST https://integration-adaptor-111.local:8443/report --resolve integration-adaptor-111.local:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/ITK_Report_request.xml" -H "Content-Type: application/xml"'

run-curl5:
	sh -c 'curl --cacert nginx/certs/ca.cer --request POST https://integration-adaptor-111.local:8443/report --resolve integration-adaptor-111.local:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/ITK_Report_request.xml" -H "Content-Type: application/xml"'

run-curl6:
	sh -c 'curl --cert nginx/certs/client_pkcs.p12 --cert-type p12 --pass password --cacert nginx/certs/ca.cer --request POST https://test02.oneoneone.nhs.uk:8443/report --resolve test02.oneoneone.nhs.uk:8443:127.0.0.1 --data "@service/src/integration-test/resources/xml/ITK_Report_request.xml" -H "Content-Type: application/xml"'

