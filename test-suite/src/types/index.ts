import { ReactNode } from "react";

export type Schema = {
  testGroups: Array<TestGroup>;
};

type TestGroup = {
  groupName: string;
  testList: Array<Test>;
};

export type Test = {
  testName: string;
  testDescription: string;
  template: string;
  testSpecifications: TestSpecs;
};

export type TestIndex = {
  [key: string]: Array<string>;
};

export interface TestSpecs {
  requestHeaderFields: Array<TestRequestField>;
  requestPayloadFields: Array<TestRequestField>;
}

export type TestRequestField = {
  id: string;
  label: string;
  defaultValue: string;
  validators?: Array<Validator>;
};

export type AppRoute = {
  path: string;
  element: ReactNode;
  name: string;
  nav?: boolean;
};

export type AppRoutes = Array<AppRoute>;

export type RequestBody = CreateReportRequestBody | UpdateReportRequestBody;

export type AdaptorRequest = {
  requestHeaderFields: RequestHeaders;
  requestPayloadFields: RequestBody;
};

export interface CreateReportRequestBody {
  name: "";
}

export interface UpdateReportRequestBody {
  name: "";
}

export type RequestHeaders = {
  "content-type": string;
  url: string;
};

export enum RequestHeaderProps {
  Header = "requestHeaderFields",
  Body = "requestPayloadFields",
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
  | "numericMatch";

export type FormErrors = {
  [key: string]: FormError | null;
};

export type FormError = {
  [key in ValidatorKeys]: Validator & { error: boolean };
};
