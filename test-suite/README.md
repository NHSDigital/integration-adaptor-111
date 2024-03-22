# Local

## Running Test Harness
### Pre-Requisites:
* Install Java 17
* Install Docker
* Install a preferred IDE

### Installing
1. Run Docker Desktop
2. Navigate to project directory via IDE Terminal
3. cd into `docker` folder
4. run command: 
```
./start-local-environment.sh
````
5. Open Docker Desktop 
6. The container structure inside Docker Desktop should display the following:
```
docker
- activemq-1
- integration-adaptor-111-1
- rabbitmq-1
- wiremock-1
````
7. cd into test-suite folder
8. run command (this will build the test harness)
```
./start-local.sh
````
9. Test harness will open in default browser

### Using the test harness:
- The test harness has data pre-populated in each form - clicking reset will undo any alternations
- ODS Codes and NHS Numbers can be freely changed from each form
- To predefine custom configurations such as ODS codes or DOS Ids, go to `integration-adaptor-111\docker\wiremock\stubs\_files\odsCodesDosIds.json`
  (rebuild the docker containers after changing the JSON file).

Example:
```json
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
- Example certifications for the test harness can be found in `integration-adaptor-111\test-suite\certificates\certs.zip`
- Upload customised certificates using `Global settings` section within the test harness

### Configuring variables within the test-harness
The ODS code, patient details, certificates, URL are all configurable. See attached screenshots for configurable features.

#### 1. Set Globals

![set-globals](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/4219fa64-5ed1-4d09-a939-916e7137d6e5)

2. Patient Referred to Primary Care for Assignment

![patient-referred-to-primary-care-for-assignment](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/1feb699d-e0c9-4517-8c82-e6cb3db81cf4)

3. Safeguarding Referral

![safeguarding-referral](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/9d158ec4-fd91-40d8-a3ec-2d5202faccaa)

4. Patient sent to A&E

![patient-sent-to-AnE](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/a8667076-5661-442e-a2e2-826b65b69158)

5. Structured FHIR Messages - Scenario 01

![structured-fhir-message-scenario-01](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/47cad87d-c803-4c95-8ddb-12fdba36177b)

6. Primary care Referral: Two Locations - Scenario 02

![primary-care-referral-two-locations-scenario-02](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/7c3f63db-f5d9-436e-a513-5f241d3e2a18)

7. Patient with no NHS no.

![patient-with-no-nhs-number](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/d3f5007c-5a2c-4e5e-b5f5-67fa46fcaeca)
   
8. Ambulance Input

![ambulance-input](https://github.com/NHSDigital/integration-adaptor-111/assets/135852870/bc69fbc4-ba0b-4fc1-900e-c426c480da73)

### Troubleshooting:

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

# Production

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