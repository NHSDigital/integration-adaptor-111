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
} from "../types";

const api = (router: Router) => {
  router.post("/api", async (req: Request, res: Response) => {
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
    const options: ReqOptions = {
      url: form.requestHeaderFields.url,
      headers: {
        "Content-Type": form.requestHeaderFields["content-type"],
      },
      agentOptions: {
        ca: fs.readFileSync(ca[0].path),
        key: fs.readFileSync(key[0].path),
        // Or use `pfx` property replacing `cert` and `key` when using private key, certificate and CA certs in PFX or PKCS12 format:
        pfx: fs.readFileSync(p12[0].path),
        passphrase: "logitech",
      },
      body: xmlPayload,
    };

    request.post(
      options,
      (error: ReqError, response: ReqResponse, body: ReqBody) => {
        const clientResponse: AdaptorResponse = {
          apiStatus: 200,
          adaptorStatus: response ? response.statusCode : 400,
          adaptorResponse: error ? error.code : body,
        };
        res.json(clientResponse).end();
      }
    );
  });
  return router;
};

export default api;
