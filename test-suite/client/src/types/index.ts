import { ReactNode } from "react";

export type Schema = {
  serverUrl: string;
  testGroups: Array<TestGroup>;
};

type TestGroup = {
  groupName: string;
  testList: Array<Test>;
};

export type Test = {
  testName: string;
  testDescription: string;
  template: {
    default: string;
  };
  testSpecifications: TestSpecs;
};

export type TestIndex = {
  [key: string]: Array<string>;
};

export interface TestSpecs {
  requestHeaderFields: Array<TestRequestField>;
  requestPayloadFields: Array<TestRequestField>;
}

export type SpecTuple = [string, Array<TestRequestField>];

export type TestRequestField = {
  id: string;
  label: string;
  defaultValue: string;
  validators?: Array<Validator>;
  placeholder?: string;
  value?: string;
};

export type AppRoute = {
  path: string;
  element: ReactNode;
  name: string;
  nav?: boolean;
};

export type AppRoutes = Array<AppRoute>;

export type Certificate = {
  name: string;
  key: string;
};

export enum RequestHeaderProps {
  Header = "requestHeaderFields",
  Body = "requestPayloadFields"
}

export type Validator = {
  precedence: number;
  id: ValidatorKeys;
  message: string;
  match: ValidatorMatch;
};

export type ValidatorMatch = boolean | string | number | null | RegExp;

export type ValidatorKeys =
  | "maxLength"
  | "minLength"
  | "notNull"
  | "alphaMatch"
  | "localMatch"
  | "reportMatch"
  | "regexMatch"
  | "isLength"
  | "numericMatch"
  | "alphaNumericMatch";

export type FormErrors = {
  [key: string]: FormError | null;
};

export type FormError = {
  [key in ValidatorKeys]: Validator & { error: boolean };
};
export type SslCerts = {
  ca: File | null;
  key: File | null;
  p12: File | null;
  password: string;
};

export enum CertificateTypes {
  CA = ".cer",
  KEY = ".key",
  P12 = ".p12"
}
