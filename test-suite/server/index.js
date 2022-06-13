const cors = require("cors");
const express = require("express");
const path = require("path");
const request = require("request");
const multiparty = require("multiparty");
const fs = require("fs");
const app = express();
const api = require("./api");
const bodyParser = require("body-parser");
const consola = require("consola");
const port = process.env.PORT || 7070;
const router = express.Router();
global.appRoot = path.resolve(__dirname);
router.use((req, res, next) => {
  consola.info({
    message: `${req.method} request from ${req.hostname} [${req.ip}]`,
    badge: true,
  });
  res.header("Access-Control-Allow-Origin", "*");
  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  next();
});
app.use(cors());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use("/", api(router, { multiparty, request, fs }));
app.listen(port, () => {
  consola.ready({
    message: `Server listening on port ${port}`,
    badge: true,
  });
});
