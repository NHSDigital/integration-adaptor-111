# integration-adaptor-111
The 111 adaptor enables GP Practices to receive [NHS 111 Report messages](https://digital.nhs.uk/developer/api-catalogue/nhs-111-hl7-v3) from NHS 111, for example into a GP system, following a patient's call to the free NHS 111 service. 

You can receive and route a “post event message” to their GP when NHS 111 requests:

* an ambulance call out
* a referral to a local emergency department
* a referral to an Out Of Hours service
* a referral to their GP

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

You need to configure the following environment variables to enable the version 1-0-0 protocol this:
* PEM111_AMQP_BROKER
* PEM111_AMQP_QUEUE_NAME
* PEM111_AMQP_USERNAME
* PEM111_AMQP_PASSWORD

If using RabbitMQ protocol 0-9-1 then the following variables need to be set:

* PEM111_AMQP_BROKER - to prevent a breaking change, the full url should be given in the form of amqp://address:port. The address section will be extracted for configuration as long as your url conforms to containing :// and :. 
* PEM111_AMQP_PORT
* PEM111_AMQP_PROTOCOL - needs to be set to "0-9-1"
* PEM111_AMQP_QUEUE_NAME
* PEM111_AMQP_USERNAME
* PEM111_AMQP_PASSWORD
* PEM111_AMQP_ROUTING_KEY
* PEM111_AMQP_SSL_ENABLED - defaults to false

### SOAP ITK

* PEM111_SOAP_VALIDATION_ENABLED - Incoming SOAP ITK message can be validated. You can decide whether you want it enabled. Supported values are `true` and `false`
* PEM111_SOAP_SEND_TO - if the validation is on then one of the checked fields is SOAP To - it's the URL of /report endpoint. You can set the expected value using this variable.

### ITK HEADER
ODS code and DOS Service ID from ITK `addresslist.address` are validated against list of supported values.
Supported ODS codes and DOS Service ID's should be separated with comma, for example `PEM111_ITK_ODS_CODE_LIST=RSHSO14A,20000729`
Values should be set in the following env variables:
* PEM111_ITK_ODS_CODE_LIST
* PEM111_ITK_DOS_ID_LIST

Alternatively you can configure the adaptor to fetch the ODS codes/DOS Ids from external server. Adaptor will read the configuration and reload it without any downtime. Following variables can be used to set up configuration service URL and the poll interval.
* PEM111_ITK_EXTERNAL_CONFIGURATION_URL
* PEM111_ITK_FETCH_INTERVAL_MIN

Configuration service has to expose a single GET endpoint returning application/json data in matching the following schema:
```
{
  "type": "object",
  "required": ["odsCodes", "dosIds"],
  "properties": {
    "odsCodes": {
      "type": "array",
      "items": {
        "type": "string"
      }
    },
    "dosIds": {
      "type": "array",
      "items": {
        "type": "string"
      }
    }
  }
}
```

Example:
```
{
   "odsCodes": [
      "EM396",
      "5L399"
   ],
   "dosIds": [
      "26428",
      "96465",
      "48583"
   ]
}
```

#### ODS code/DOS ID validation rules
- At least one property of `PEM111_ITK_ODS_CODE_LIST`, `PEM111_ITK_DOS_ID_LIST`, `PEM111_ITK_EXTERNAL_CONFIGURATION_URL` has to be set. Adaptor will fail to start if none is provided.
- If you provide external service URL as well as ODS/DOS lists then external configuration overrides the defined lists.
- Adaptor validates provided external service URL on startup. If the endpoint is unreachable, does not respond with HTTP 200 or responds with invalid data then adaptor will fail to start.
- Incoming ITK message will be accepted when either ODS code or DOS Id is correct.


### TLS Mutual Authentication
Nginx proxy is used to handle TLS MA. In order to configure it you need to set the following env variables:
* NGINX_PUBLIC_CERT - Server public certificate
* NGINX_PRIVATE_CERT - Server private certificate
* NGINX_CLIENT_PUBLIC_CERT - Client public certificate
* NGINX_CA_CERT - Root certificate
* NGINX_CRL - Certificate revocation list
* NGINX_CRL_URL - CRL URL - Nginx can download CRL on startup

## ITK to FHIR Mapping
Even though the adaptor removes this complexity, the [FHIR field mappings have been documented for information](doc/ITK_FHIR_mapping.md).

## ITK Testbench
The [ITK Testbench](https://digital.nhs.uk/services/interoperability-toolkit/developer-resources/itk-test-centre/itk-testbench) provided by NHS Digital has an essential tool for ITK interface development and conformance.  This tool provides resources that will allow you to adequately test the 111 adaptor within your own environment.

## Test Pack
Our testing pack as well our test harness can be found inside the test-suite directory [Test-Suite](./test-suite)

## NIA Support (NHS England) guidance
If you are looking to make changes to the adaptor you should first read the [guidance for developing the adaptor](nhs-england-developer-information.md).