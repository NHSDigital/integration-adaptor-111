#Production

### Installing

Run the shell script

```shell
sh start-prod.sh
```

If you do not have nodejs installed as a CLI, you may get an error.

The error will point you to the nodejs website, alternatively you can use
some other syntaxes depending on your operating system:

```shell
sudo apt-get install nodejs
yum install nodejs
brew install nodejs
```

The server and client should run on the same EC2/Cloud server together, as the clients connection to the test suite backend has been hard-coded

The script has automated the process of building the client into a local ./html folder, aswell as running the backend service for you

Ensure these files are moved to your nginx (or other web server) html folder. A sample of the following snippet below should do this:

```shell
mv html/* /usr/share/nginx/html/
```

The test suite backend service is managed by a node package called PM2. You can find further usage guidelines here:
https://pm2.keymetrics.io/docs/usage/quick-start/

