# Developer Information

> [!WARNING]
> The audience of this documentation is designed for NHS England developers.

## Pre requisites for running the Adaptor:
1. JDK 21
2. Docker

## How to run service:
* Navigate to `docker`
* Run script: `./start-local-environment.sh`

The above script builds the necessary docker images and starts the SpringBoot service and ActiveMQ
* REST service `localhost:10001`
* ActiveMQ: `localhost:5672`

## How to run unit tests:
* Navigate to `service`
* Run: `./gradlew test`

## How to run integration tests:
* Navigate to `service`
* Run: `./gradlew integrationTest`

## Example bundle message
* Example bundle messages can be found inside of [/doc/json folder](doc/json) mapped from similarly named [ITK files within the /doc/xml folder](doc/xml).

## AMQP Variables
* Queue name: `encounter-report`
* Broker: `amqp://activemq:5672`
