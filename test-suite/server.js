const cors = require('cors');
const express = require('express');
const http = require('http');
const path = require('path');
const { XMLHttpRequest } = require('xmlhttprequest');
const fs = require('fs');
const tls = require('tls');
const app = express();
const bodyParser = require('body-parser');
global.appRoot = path.resolve(__dirname);
const consola = require('consola');
const { json } = require('express/lib/response');
const port = process.env.PORT || 7070;
const router = express.Router();
app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

router.use(function (req, res, next) {
  consola.info({
    message: `${req.method} request from ${req.hostname} [${req.ip}]`,
    badge: true
  });
  res.header('Access-Control-Allow-Origin', '*');
  res.header(
    'Access-Control-Allow-Headers',
    'Origin, X-Requested-With, Content-Type, Accept'
  );
  next();
});

router.post('/api', function (req, res) {
  const { template, form } = req.body;
  const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
    (acc, [k, v]) => acc.replace(`@@${k}@@`, v),
    template
  );
  const xmlReq = new Promise((resolve) => {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.requestHeaderFields.url, true);
    xhr.setRequestHeader(
      'Content-Type',
      form.requestHeaderFields['content-type']
    );
    xhr.setRequestHeader('Access-Control-Allow-Origin', '*');
    xhr.onreadystatechange = function () {
      if (this.readyState === 4) {
        const { status, responseText } = this;
        resolve({
          status,
          responseText
        });
      }
    };
    xhr.send(xmlPayload);
  });
  xmlReq.then((xmlRes) => {
    res
      .json({
        apiStatus: 200,
        adaptorStatus: xmlRes.status,
        adaptorResponse: xmlRes.responseText
      })
      .end();
  });
});

// const sniDefaultCert = fs.readFileSync(
//   path.join(__dirname, 'certs/normal.crt')
// );
// const sniDefaultKey = fs.readFileSync(path.join(__dirname, 'certs/normal.key'));

const sniCallback = (serverName, callback) => {
  console.log(serverName);
  // let cert = null;
  // let key = null;

  // if (serverName === 'testing.com') {
  //   cert = fs.readFileSync(path.join(__dirname, 'certs/testing.crt'));
  //   key = fs.readFileSync(path.join(__dirname, 'certs/testing.key'));
  // } else {
  //   cert = sniDefaultCert;
  //   key = sniDefaultKey;
  // }

  // callback(
  //   null,
  //   new tls.createSecureContext({
  //     cert,
  //     key
  //   })
  // );
};

const serverOptions = {
  SNICallback: sniCallback,

  // Optional: TLS Versions
  maxVersion: 'TLSv1.3',
  minVersion: 'TLSv1.2'
};

app.use('/', router);
const server = http.Server(serverOptions, app);
// app.listen(port);

// Start the Server
server.listen(port, () => {
  consola.ready({
    message: `Server listening on port ${port}`,
    badge: true
  });
});
