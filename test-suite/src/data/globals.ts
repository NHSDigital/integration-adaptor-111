import { TestRequestField } from "../types";
import { isUrl, minLength, notNull } from "../utils/validators";

/**
 * Ensure global fields match fields in schema, with the exact same validators
 * else the input it's trying to associate with will break.
 */
const globals: Array<TestRequestField> = [
  {
    label: "Url",
    id: "url",
    defaultValue: "http://localhost:8081/report",
    validators: [isUrl()],
  },
  {
    label: "Content Type",
    id: "content-type",
    defaultValue: "application/xml",
    validators: [notNull(), minLength(7)],
  },
];

export default globals;
