# integration-adaptor-111
National Integration Adaptors - Adaptor for NHS 111 Post Event Message

## Requirements:
1. JDK 14
2. Docker

## How to run service:
* Navigate to `ops/local`
* Run script: `start-local-environment.sh`

The above script builds necessary docker images and starts the SpringBoot service and ActiveMQ
* REST service `localhost:10001`
* ActiveMQ: `localhost:5672`

## How to run unit tests:
* Navigate to `service`
* Run: `./gradlew test`

## How to run integration tests:
* Navigate to `service`
* Run: `./gradlew integrationTest`

## Example bundle message 
* An example bundle message can be found [here](./example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR. 