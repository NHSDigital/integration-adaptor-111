# integration-adaptor-111
National Integration Adaptors - Adaptor for NHS 111 Post Event Message

## How to use this image

### Requirements:
1. JDK 14
2. Docker

### Clone the repository

`$ git clone https://github.com/nhsconnect/integration-adaptor-111.git`

### Find the scripts folder

`$ cd integration-adaptor-111/test-scripts`

Each release has its own folder, use the scripts for specific release

`$ cd 0.1.0`

### Start it up

`integration-adaptor-111/test-scripts/0.1.0$ ./run.sh`

This will build the application container locally and start it along with ActiveMQ
The services will listen on following ports

* REST service `localhost:8080`
* ActiveMQ: `localhost:5672`

## Testing

## How to run unit tests:
* Navigate to `integration-adaptor-111/service`
* Run: `./gradlew test`

## How to run integration tests:
* Navigate to `integration-adaptor-111/service`
* Run: `./gradlew integrationTest`

## How to run the acceptance test:
* Navigate to `integration-adaptor-111/test-scripts/0.1.0/tests/`
* Run ./acceptance.sh

## Example bundle message 
* An example bundle message can be found [here](./example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR. 

### Stopping the adapter:

`$ docker-compose down`