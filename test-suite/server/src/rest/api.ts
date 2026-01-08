import { Request as ExpressRequest, Response, Router } from "express";
import axios, { AxiosError } from "axios";
import multiparty from "multiparty";
import fs from "fs";
import https from "https";
import {
  MultiPartForm,
  FileTuple,
} from "../types";
import { AdaptorRequest, AdaptorResponse } from "@server/types";

const api = (router: Router) => {
  router.post("/report", async (req: ExpressRequest, res: Response) => {
    const formPromise: Promise<MultiPartForm> = new Promise((resolve) => {
      const multipartForm = new multiparty.Form();
      multipartForm.parse(req, (_err, fields, files) => {
        resolve({ fields, files });
      });
    });
    const { fields, files } = await formPromise;
    const { form: json, template, password } = fields;
    const { ca, key, p12 } = files;
    const form: AdaptorRequest = JSON.parse(json);
    const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
      (acc, [k, v]) => acc.replace(`@@${k}@@`, v),
      template[0]
    );

    const { url } = form.requestHeaderFields;
    const isHttps = url.startsWith("https");
    const caCert = getAgentKey(ca);
    const caKey = getAgentKey(key);
    const p12Key = getAgentKey(p12);
    let httpsAgent: https.Agent | undefined;

    if (isHttps) {
      if (!caCert || !caKey || !p12Key) {
        const rejectResponse: AdaptorResponse = {
          apiStatus: 400,
          adaptorStatus: 400,
          message: "HTTPS_MISSING_CERTS",
        };
        res.json(rejectResponse).end();
        return;
      }

      const agentOptions: https.AgentOptions = {
        ca: caCert,
        key: caKey,
        pfx: p12Key,
        passphrase: password ? password[0] : undefined,
        rejectUnauthorized: true,
      };

      httpsAgent = new https.Agent(agentOptions);
    }

    try {
      const response = await axios.post<string>(url, xmlPayload, {
        headers: {
          "Content-Type": form.requestHeaderFields["content-type"],
          "Content-Length": Buffer.byteLength(xmlPayload, "utf8"),
        },
        httpsAgent,
        responseType: "text",
        transformResponse: [(data) => data],
        validateStatus: () => true,
      });

      const adaptorResponse: AdaptorResponse = {
        apiStatus: 200,
        adaptorStatus: response.status,
        message: response.data ?? "",
      };

      res.json(adaptorResponse).end();
    } catch (e) {
      const err = e as AxiosError;

      const adaptorResponse: AdaptorResponse = {
        apiStatus: 200,
        adaptorStatus: err.response?.status ?? (err as any)?.errno ?? 400,
        message: err.code ?? err.message ?? "UNKNOWN_ERROR",
      };

      res.json(adaptorResponse).end();
    }
  });
  return router;
};

const getAgentKey = (file: FileTuple | undefined) =>
  file && Array.isArray(file) && file.length && file[0].path
    ? fs.readFileSync(file[0].path)
    : undefined;

export default api;