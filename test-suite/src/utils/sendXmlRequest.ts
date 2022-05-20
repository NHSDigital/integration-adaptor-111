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
  return new Promise((res) => {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", form.requestHeaderFields.url, true);
    xhr.setRequestHeader(
      "Content-Type",
      form.requestHeaderFields["content-type"]
    );
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
    const xmlPayload = Object.entries(form.requestPayloadFields).reduce(
      (acc, [k, v]) => {
        const templateKey = `%${k.toUpperCase()}%`;
        console.log(templateKey);
        const newXml = acc.replace(templateKey, v);
        return newXml;
      },
      xml
    );
    xhr.onreadystatechange = function () {
      if (this.readyState === XMLHttpRequest.DONE) {
        res({
          xml: beautify(this.response),
          status: this.status,
        });
      }
    };
    console.log(xmlPayload);
    xhr.send(xmlPayload);
  });
};

export default sendXmlRequest;
