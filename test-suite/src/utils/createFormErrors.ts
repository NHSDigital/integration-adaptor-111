import { FormError, FormErrors, RequestHeaderProps, TestSpecs } from "../types";

const createFormErrors = (specs: TestSpecs): FormErrors => {
  return [RequestHeaderProps.Body, RequestHeaderProps.Header].reduce(
    // Flatten Header & Body fields into one object
    (acc, key) => ({
      ...acc,
      ...specs[key].reduce(
        // Assign each input field a FormError object
        (accu, valu) => ({
          ...accu,
          [valu.id]: valu.validators
            ? valu.validators.reduce(
                // Convert Validator array into key:value FormError pairs
                (accum, value) => ({
                  ...accum,
                  [value.id]: { ...value, error: false },
                }),
                {} as FormError
              )
            : null,
        }),
        {} as FormErrors
      ),
    }),
    {} as FormErrors
  );
};
export default createFormErrors;
