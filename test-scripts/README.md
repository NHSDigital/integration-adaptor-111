# Quick reference
- Maintained by: NHS Digital
- Where to get help: https://github.com/nhsconnect/integration-adaptor-111
- Where to file issues: https://github.com/nhsconnect/integration-adaptor-111/issues
  
## How to use this image

### Clone the repository

`$ git clone https://github.com/nhsconnect/integration-adaptor-111.git`

### Find the scripts folder

`$ cd integration-adaptor-111/test-scripts`

Each release has its own folder, use the scripts for specific release

`$ cd 0.5.1`

### Start it up

`integration-adaptor-111/test-scripts/0.5.1$ ./run.sh`

This will build the application container locally and start it along with ActiveMQ
The services will listen on following ports

* REST service `localhost:8080`
* ActiveMQ: `localhost:5672`

## Testing

## How to run the acceptance test:
* Navigate to `integration-adaptor-111/test-scripts/0.5.1/tests/`
* Run ./acceptance.sh

## Example bundle message 
* An example bundle message can be found [here](./example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR. 

### Stopping the adapter:

`$ docker-compose down`