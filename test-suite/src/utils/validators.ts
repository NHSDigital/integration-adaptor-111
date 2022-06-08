import {
  AdaptorRequest,
  FormError,
  FormErrors,
  MappedStrings,
  Validator,
  ValidatorKeys,
} from "../types";

export const notNull = (): Validator => ({
  precedence: 0,
  id: "notNull",
  match: null,
  message: "This field must not be empty",
});

export const maxLength = (int: number): Validator => ({
  precedence: 2,
  id: "maxLength",
  match: int,
  message: `This field has a maximum length of ${int}`,
});

export const minLength = (int: number): Validator => ({
  precedence: 2,
  id: "minLength",
  match: int,
  message: `This field has a minimum length of ${int}`,
});

export const isLength = (int: number): Validator => ({
  precedence: 2,
  id: "isLength",
  match: int,
  message: `This field requires a length of ${int}`,
});

export const regexMatch = (
  regexp: RegExp,
  message: string,
  id: ValidatorKeys = "regexMatch" as const,
  precedence = 1
): Validator => ({
  precedence,
  id,
  match: regexp,
  message: message,
});

export const isReportUrl = () =>
  regexMatch(
    /((\/){1}(report)){1}/g,
    "Field must use the /report subdirectory",
    "reportMatch" as const,
    2
  );

export const isUrl = () =>
  regexMatch(
    /^https?:\/\/\w+(\.\w+)*(:[0-9]+)?(\/.*)?$/g,
    "Field must be a URL",
    "reportMatch" as const,
    1
  );

export const isAlpha = () =>
  regexMatch(
    /^[A-Za-z]+$/,
    "Field must be alphabetical",
    "alphaMatch" as const
  );

export const isNumeric = () =>
  regexMatch(/^[0-9 ]*$/, "Field must be numeric", "numericMatch" as const);

export const isAlphanumeric = () =>
  regexMatch(
    /^[A-Za-z0-9 ]*$/,
    "Field must be alphanumeric",
    "alphaNumericMatch" as const
  );

export const validateField = (fieldValidation: FormError, value: string) =>
  Object.entries(fieldValidation).reduce((acc, [k, v]) => {
    let isError = false;
    if (k === "notNull") {
      isError = value === null || value === "";
    } else if (k === "maxLength") {
      isError = typeof v.match === "number" && value.length > v.match;
    } else if (k === "minLength") {
      isError = typeof v.match === "number" && value.length < v.match;
    } else if (k === "hasLength") {
      isError = typeof v.match === "number" && value.length === v.match;
    } else {
      isError = v.match instanceof RegExp && !new RegExp(v.match).test(value);
    }
    return {
      ...acc,
      [k]: {
        ...v,
        error: isError,
      },
    };
  }, {} as FormError);
