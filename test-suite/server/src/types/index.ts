import { File } from "multiparty";

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
