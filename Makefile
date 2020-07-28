.PHONY: build

build: build-adapter build-tests build-nginx

build-nginx:
	sh -c 'docker build -t local/111-nginx -f Dockerfile.nginx .'

build-adapter:
	sh -c 'docker build -t local/111-adapter -f Dockerfile.111 .'

build-tests:
	sh -c 'docker build -t local/111-tests -f Dockerfile.tests .'

run-all:
	sh -c 'docker-compose -f docker-compose.yml run integration-adaptor-111'

clean:
	sh -c 'echo "TODO clean"'
