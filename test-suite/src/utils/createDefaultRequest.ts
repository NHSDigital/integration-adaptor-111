import {
  AdaptorRequest,
  RequestBody,
  RequestHeaderProps,
  RequestHeaders,
  TestSpecs,
} from "../types";

const createDefaultRequest = (specs: TestSpecs): AdaptorRequest =>
  [RequestHeaderProps.Header, RequestHeaderProps.Body].reduce((acc, val) => {
    // Flatten test specs into key:value containing default input
    return {
      ...acc,
      [val]: specs[val].reduce(
        (acc, val) => ({
          ...acc,
          [val.id]: val.defaultValue,
        }),
        {} as RequestHeaders | RequestBody
      ),
    };
  }, {} as AdaptorRequest);

export default createDefaultRequest;
