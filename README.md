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

The 111 adaptor consists of two docker images:[proxy](https://hub.docker.com/r/nhsdev/nia-111-nginx-adaptor) and [adaptor](https://hub.docker.com/r/nhsdev/nia-111-adaptor), you must deploy it within your own environment.

The following diagram illustrates the NHS 111 Report adaptor: 
![111 SysContext](/img/111SysContext.png)

## Configuration
The adaptor reads its configuration from environment variables. The following sections describe the environment variables used to configure the adaptor.

### Inbound Queue Configuration
The post event messages (PEM) handled by the adaptor are sent to the Active Message Queue within the GP supplier's own environment and are not stored within the NHS 111 adaptor itself.  

You need to configure the following environment variables to enable this:
* PEM111_AMQP_BROKER
* PEM111_AMQP_QUEUE_NAME
* PEM111_AMQP_USERNAME
* PEM111_AMQP_PASSWORD

### SOAP ITK
Incoming SOAP ITK message is validated. One of the requirements is to check SOAP To field - it's the URL of /report endpoint. You can set the expected value using the following env variable:
* PEM111_SOAP_SEND_TO

### ITK HEADER
ODS code and DOS Service ID from ITK `addresslist.address` are validated against list of supported values.
Supported ODS codes and DOS Service ID's should be separated with comma, for example `PEM111_ITK_ODS_CODE_LIST=RSHSO14A,20000729`
Values should be set in the following env variables:
* PEM111_ITK_ODS_CODE_LIST
* PEM111_ITK_DOS_ID_LIST

### TLS Mutual Authentication
Nginx proxy is used to handle TLS MA. In order to configure it you need to set the following env variables:
* NGINX_PUBLIC_CERT - Server public certificate
* NGINX_PRIVATE_CERT - Server private certificate
* NGINX_CLIENT_PUBLIC_CERT - Client public certificate
* NGINX_CA_CERT - Root certificate
* NGINX_CRL - Certificate revocation list
* NGINX_CRL_URL - CRL URL - Nginx can download CRL on startup

## ITK to FHIR Mapping
Even though the adaptor removes this complexity, the FHIR field mappings have been documented [here](doc/ITK_FHIR_mapping.docx) for information.

## Running the Adaptor

### Pre requisites for running the Adaptor:
1. JDK 14
2. Docker

### How to run service:
* In root directory
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
* An example bundle message can be found [here](doc/example_FHIR_bundle_message.json) of [ITK_Report](./service/src/integration-test/resources/xml/ITK_Report_request.xml) converted to FHIR.

### AMQP Variables
* Queue name: `encounter-report`
* Broker: `amqp://activemq:5672`

## ITK Testbench
The [ITK Testbench](https://digital.nhs.uk/services/interoperability-toolkit/developer-resources/itk-test-centre/itk-testbench) provided by NHS Digital has an essential tool for ITK interface development and conformance.  This tool provides resources that will allow you to adequately test the 111 adaptor within your own environment.
