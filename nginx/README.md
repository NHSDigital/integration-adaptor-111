# NGINX Reverse proxy container

The 111 application by itself does not offer TLS termination. If there is a need for it you can use the container with nginx that will terminate the TLS and provide authentication.

* The `conf` directory contains configuration file for nginx
* The `scripts` directory contains scripts used to setup the container and a script to source the certificates to environment variables (if you use the make scripts this is not needed)
* the `certs` directory should contain a set of certificates for te nginx, there are not included in the repo, for local development you have to create your own set or copy the ones already created to that directory.

## Creation of certificates

1. Create the Root CA:

  > `openssl req -newkey rsa:4096 -keyform PEM -keyout ca.key -x509 -days 3650 -outform PEM -out ca.cer`
  
2. Create the Server Private Key

  > `openssl genrsa -out server_private.key 4096`

3. Create the CSR with private key for Root CA

  > `openssl req -new -key server_private.key -out server.csr -sha256`

4. Create the Server Public Certificate

  > `openssl x509 -req -in server.csr -CA ca.cer -CAkey ca.key -set_serial 100 -extensions server -days 1460 -outform PEM -out server_public.cer -sha256`

5. Client the client private key

  > `openssl genrsa -out client_private.key 4096`

6. Create the CSR with client private key

  > `openssl req -new -key client.key -out client.csr`

7. Create the client public certificate

  > `openssl x509 -req -in client.csr -CA ca.cer -CAkey ca.key -set_serial 101 -extensions client -days 365 -outform PEM -out client_public.cer`

8. Create a PKCS12 cert for client

  > `openssl pkcs12 -export -inkey client.key -in client.cer -out client.p12`

## Running the local environment

There is `Makefile` in top directory with command tied to keywords to simplify the workflow.

* `make build` will build all the required containers, `integration-adaptor-111`, `nginx` and `test-111`.
* `make run-all` will run all required containers defined in `docker-compose.yml`. There are depedencies set between containers so starting the `nginx` container will start also the `integration-adaptor-111` and `activemq`.
* Once the startup finises you can `make status` which will show all running containers. You should see all three running with relevant ports mapped to host ports.
* If a containers shows that exited right after startup you can check its logs with `docker logs [container_id]`.
* There are 3 predefined requests that use curl:
    1. `make run-curl` will run a simple GET via nginx, without the client certificate - should result in Error 403
    2. `make run-curl2` will run a simple GET via nginx, with the client certificate - should result in 404 returned by application - which indicates that the traffic hoes through.
    3. `make run-curl3` similar to above but queries the `auth_test` endpoint in nginx which will show the certificates that are used.
    4. `make run-curl4` runs a correct POST to application, should result in correct SOAP response
    5. `make run-curl5` similar to above but without the client certificate provided, should result in 403 error.
