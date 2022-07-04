/**
 * Shared Types
 *
 * Usage on both the client & server:
 * import { Form } from "@server/types"
 */

export type Form = {
  [key: string]: string;
};
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
  message: string;
};
