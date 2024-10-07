# Developer Information

> [!WARNING]
> The audience of this documentation is designed for NHS England developers.

## Pre requisites for running the Adaptor:
1. JDK 17
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
* An example bundle message can be found [here](doc/example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR.

## AMQP Variables
* Queue name: `encounter-report`
* Broker: `amqp://activemq:5672`
