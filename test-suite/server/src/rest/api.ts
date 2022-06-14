import { Request as Request, Response, Router } from "express";
import request from "request";
import multiparty from "multiparty";
import fs from "fs";
import {
  AdaptorRequest,
  ReqBody,
  ReqError,
  ReqOptions,
  ReqResponse,
  AdaptorResponse,
  MultiPartForm,
  FileTuple,
} from "../types";

const api = (router: Router) => {
  router.post("/report", async (req: Request, res: Response) => {
    const formPromise: Promise<MultiPartForm> = new Promise((resolve) => {
      const multipartForm = new multiparty.Form();
      multipartForm.parse(req, (_err, fields, files) => {
        resolve({
          fields,
          files,
        });
      });
    });
    const { fields, files } = await formPromise;
    const { form: json, template } = fields;
    const { ca, key, p12 } = files;
    const form: AdaptorRequest = JSON.parse(json);
    const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
      (acc, [k, v]) => acc.replace(`@@${k}@@`, v),
      template[0]
    );

    const { url } = form.requestHeaderFields;
    const requiresCert = url.includes("https");
    const caCert = getAgentKey(ca);
    const caKey = getAgentKey(key);
    const p12Key = getAgentKey(p12);
    if (requiresCert && (!caCert || !caKey || !p12Key)) {
      const rejectResponse: AdaptorResponse = {
        apiStatus: 400,
        adaptorStatus: 400,
        message: "API_MISSING_CERTS",
      };
      res.json(rejectResponse).end();
    }

    const options: ReqOptions = {
      url,
      headers: {
        "Content-Type": form.requestHeaderFields["content-type"],
        "Content-Length": xmlPayload.length,
      },
      agentOptions: {
        ca: getAgentKey(ca),
        key: getAgentKey(key),
        // Or use `pfx` property replacing `cert` and `key` when using private key, certificate and CA certs in PFX or PKCS12 format:
        pfx: getAgentKey(p12),
        passphrase: "logitech",
        rejectUnauthorized: false,
      },
      body: xmlPayload,
    };
    request.post(
      options,
      (error: ReqError, response: ReqResponse, body: ReqBody) => {
        let adaptorResponse: AdaptorResponse = {
          apiStatus: 200,
          adaptorStatus: 400,
          message: error ? error.code : "UNKNOWN_ERROR",
        };
        if (response && body) {
          adaptorResponse = {
            apiStatus: 200,
            adaptorStatus: response.statusCode,
            message: body,
          };
        }
        res.json(adaptorResponse).end();
      }
    );
  });
  return router;
};

const getAgentKey = (file: FileTuple | undefined) =>
  file && Array.isArray(file) && file.length && file[0].path
    ? fs.readFileSync(file[0].path)
    : undefined;

export default api;
