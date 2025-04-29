## Local

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