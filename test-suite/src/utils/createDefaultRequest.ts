import {
  AdaptorRequest,
  RequestBody,
  RequestHeaderProps,
  RequestHeaders,
  TestRequestField,
  TestSpecs,
} from "../types";

const createDefaultRequest = (
  specs: TestSpecs,
  globals: Array<TestRequestField>
): AdaptorRequest =>
  [RequestHeaderProps.Header, RequestHeaderProps.Body].reduce((acc, val) => {
    // Flatten test specs into key:value containing default input
    return {
      ...acc,
      [val]: specs[val].reduce((acc, val) => {
        const global = globals.find((g) => g.id === val.id);
        return {
          ...acc,
          [val.id]: global ? global.value : val.defaultValue,
        };
      }, {} as RequestHeaders | RequestBody),
    };
  }, {} as AdaptorRequest);

export default createDefaultRequest;
