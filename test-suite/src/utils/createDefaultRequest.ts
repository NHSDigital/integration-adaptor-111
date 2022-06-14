import {
  AdaptorRequest,
  MappedStrings,
  RequestHeaderProps,
  TestRequestField,
  TestSpecs
} from "../types";

const createDefaultRequest = (
  specs: TestSpecs,
  globals: Array<TestRequestField>
): AdaptorRequest =>
  [RequestHeaderProps.Header, RequestHeaderProps.Body].reduce((acc, val) => {
    // Flatten test specs into key:value containing default input
    const arr: Array<TestRequestField> = specs[val];
    return {
      ...acc,
      [val]: arr.reduce((acc, valu) => {
        const global = globals.find((g) => g.id === valu.id);
        return {
          ...acc,
          [valu.id]: global && global.value ? global.value : valu.defaultValue
        };
      }, {} as MappedStrings)
    };
  }, {} as AdaptorRequest);

export default createDefaultRequest;
