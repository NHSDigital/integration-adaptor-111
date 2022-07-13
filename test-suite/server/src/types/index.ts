import { File } from "multiparty";
import { Options, Response, Request } from "request";

export type ReqBody = Response["body"] | undefined;
export type ReqOptions = Options;
export type ReqResponse = Response | undefined;
export type ReqRequest = Request;
export type ReqError = {
  errno?: number;
  code?: string;
  syscall?: string;
  address?: string;
  port: number;
} | null;

export type FileTuple = [File];
type StringTuple = [string];
type FormFields = {
  form: string;
  template: StringTuple;
  password?: Array<string>;
};

type FormFiles = {
  ca?: FileTuple;
  key?: FileTuple;
  p12?: FileTuple;
  password?: string;
};

export type MultiPartForm = { fields: FormFields; files: FormFiles };
