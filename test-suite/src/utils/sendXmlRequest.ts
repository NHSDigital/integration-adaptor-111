import { AdaptorRequest } from "../types";
const beautify = require("xml-beautifier");

export type AdaptorResponse = {
  xml: string;
  status: number;
};

const sendXmlRequest = async (
  form: AdaptorRequest,
  template: string
): Promise<AdaptorResponse> => {
  const reportReq = await fetch(template);
  const xml = await reportReq.text();
  return new Promise((resolve) => {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", form.requestHeaderFields.url, true);
    Object.entries(form.requestHeaderFields).forEach(([k, v]) => {
      xhr.setRequestHeader(k, v);
    });
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
    const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
      (acc, [k, v]) => {
        const templateKey = `@@${k}@@`;
        const newXml = acc.replaceAll(templateKey, v);
        return newXml;
      },
      xml
    );
    xhr.onreadystatechange = function () {
      if (this.readyState === XMLHttpRequest.DONE) {
        resolve({
          xml: beautify(this.response),
          status: this.status,
        });
      }
    };
    xhr.send(xmlPayload);
  });
};

export default sendXmlRequest;
