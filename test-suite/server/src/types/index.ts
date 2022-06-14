import { File } from "multiparty";
import { Options, Response, Request } from "request";

export type ReqBody = Response["body"];
export type ReqOptions = Options;
export type ReqResponse = Response;
export type ReqRequest = Request;
export type ReqError = { code: string };

type FileTuple = [File];
type StringTuple = [string];
type FormFields = {
  form: string;
  template: StringTuple;
};

type FormFiles = {
  ca: FileTuple;
  key: FileTuple;
  p12: FileTuple;
};

export type Form = {
  [key: string]: string;
};

export type MultiPartForm = { fields: FormFields; files: FormFiles };

export type RequestHeaders = {
  "content-type": string;
  url: string;
};

export type AdaptorRequest = {
  requestHeaderFields: RequestHeaders;
  requestPayloadFields: Form;
};

export type AdaptorResponse = {
  apiStatus: number;
  adaptorStatus: number;
  adaptorResponse: string;
};
