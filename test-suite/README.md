## Local

### Prerequisites

To run the testing scenarios, you will need to ensure that you have version of the adaptor and its dependencies.

You can use the `start-local-environment.sh` script located in the `docker` folder to ensure that the no configuration changes have to be made.

### Installing

Run the shell script for local development:

```shell
sh start-local.sh
```

If you do not have nodejs installed as a CLI, you will get an error.

The error will point you to the nodejs website, alternatively you can use
some other syntaxes depending on your operating system:

```shell
sudo apt-get install nodejs
yum install nodejs
brew install nodejs
```

### Running

When the local script is successful, the client should spawn in your browser, if not you can find this on http://localhost:3000.

The test suite backend server will be running on port 7070. You shouldn't be required to manage this connection, as the client will have a hard-coded connection to it.

You can check if the backend instance is active by running:

```shell
curl -v http://localhost:7070/healthcheck
```

#### Performing Tests

In the UI:

* Scroll down to the test you want to perform and click `Run Test`.
* This will display a form, which you can send as is, or adjust the values according to your specific test requirements.
* Click the `Send` button, and wait for the test to complete.

Once completed, you should see a response box in the browser which resembles this:

```
API Status: 200
Adaptor Status: 200

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
    <soap:Header>
        <wsa:MessageID>
            93466F14-221D-417C-83A9-DE9CAAD0A68D
        </wsa:MessageID>
        <wsa:Action>
            urn:nhs-itk:services:201005:SendNHS111Report-v2-0Response
        </wsa:Action>
    </soap:Header>
    <soap:Body>
        <itk:SimpleMessageResponse xmlns:itk="urn:nhs-itk:ns:201005">
            OK:2B77B3F5-3016-4A6D-821F-152CE420E58D
        </itk:SimpleMessageResponse>
    </soap:Body>
</soap:Envelope>
```

You can then retrieve the FHIR message from your running activeMQ instance on the `encounter-report` queue.

## Production

### Installing

Run the shell script for production:

```shell
sh start-prod.sh
```

Similar to running locally, If you do not have nodejs installed as a CLI, you will get an error.

The error will point you to the nodejs website, alternatively you can use
some other syntaxes depending on your operating system:

```shell
sudo apt-get install nodejs
yum install nodejs
brew install nodejs
```

### Running

The server and client should run on the same EC2/Cloud server together, as the clients connection to the test suite backend has been hard-coded.

The script has automated the process of building the client into a local ./html folder on the root directory of the test suite, aswell as running the backend service for you.

Ensure these files are moved to your nginx (or other web server) html folder. A sample of the following snippet below should do this:

```shell
mv html/* /usr/share/nginx/html/
```

The test suite backend service is managed by a node package called PM2. You can find further usage guidelines here:
https://pm2.keymetrics.io/docs/usage/quick-start/

You can check if the backend instance is active by running:

```shell
curl -v http://localhost:7070/healthcheck
```