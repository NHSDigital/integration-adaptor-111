# integration-adaptor-111
The 111 adaptor enables GP Practices to receive [NHS 111 Report messages](https://digital.nhs.uk/developer/api-catalogue/nhs-111-hl7-v3) from NHS 111, for example into a GP system, following a patient's call to the free NHS 111 service. 

You can receive and route a “post event message” to their GP when NHS 111 requests:

an ambulance call out
a referral to a local emergency department
a referral to an Out Of Hours service
a referral to their GP
The NHS 111 Report adaptor can receive messages from the NHS 111 service and post them to the mailbox of the relevant GP practice.

## Adaptor Scope
The main objective of the 111 Adaptor is to hide complex legacy standards and instead present a simple and consistent interface aligned to current NHSD national standards.  The adaptor receives ITK 2.2 wrapped Clinical Document Architecture (CDA) XML documents over web services, and converts them into structured FHIR messages before posting them onto the GP system's inbound event queue.

As with all National Integration Adaptors, the 111 adaptor is a self-hosted component - packaged as a [docker image](https://hub.docker.com/r/nhsdev/nia-111-adaptor), you must deploy it within your own environment.

The following diagram illustrates the NHS 111 Report adaptor: 
![111 Logical Architecture](img/111 SysContext.png)

## Configuration
The adaptor reads its configuration from environment variables. The following sections describe the environment variables used to configure the adaptor.

### Inbound Queue Configuration
The post event messages (PEM) handled by the adaptor are sent to the Active Message Queue within the GP supplier's own environment and are not stored within the NHS 111 adaptor itself.  

You need to configure the following environment variables to enable this:
/n PEM111_AMQP_BROKER
/n PEM111_AMQP_QUEUE_NAME
/n PEM111_AMQP_USERNAME
/n PEM111_AMQP_PASSWORD

## ITK to FHIR Mapping
Even though the adaptor removes this complexity, the FHIR field mappings have been documented in this [ReadME]() for information.

## Running the Adaptor

### Pre requisites for running the Adaptor:
1. JDK 14
2. Docker

### How to run service:
* Navigate to `ops/local`
* Run script: `start-local-environment.sh`

The above script builds necessary docker images and starts the SpringBoot service and ActiveMQ
* REST service `localhost:10001`
* ActiveMQ: `localhost:5672`

### How to run unit tests:
* Navigate to `service`
* Run: `./gradlew test`

### How to run integration tests:
* Navigate to `service`
* Run: `./gradlew integrationTest`

### Example bundle message 
* An example bundle message can be found [here](./example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR.

### AMQP Variables
* Queue name: `encounter-report`
* Broker: `amqp://activemq:5672`
