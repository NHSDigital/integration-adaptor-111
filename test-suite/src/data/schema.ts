import { RequestHeaderProps, Schema } from "../types";
import {
  isLength,
  isAlpha,
  isNumeric,
  isReportUrl,
  minLength,
  notNull,
  isUrl,
} from "../utils/validators";

/**
 * Ensure the id of the testSpecifications field matches the
 * placeholder in the XML template file, for example 'first-name'
 * would be templated with '@@first-name@@' in the XML file.
 */
const schema: Schema = {
  testGroups: [
    {
      groupName: "Report",
      testList: [
        {
          testName: "Create Report",
          testDescription:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a porta sem, at posuere velit. Donec tempor non ligula et congue. Donec faucibus lobortis lorem, et luctus lacus efficitur at. ",
          template: require("./create-report-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
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
            ],
            [RequestHeaderProps.Body]: [
              {
                label: "First Name",
                id: "first-name",
                defaultValue: "John",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Last Name",
                id: "last-name",
                defaultValue: "Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Message ID",
                id: "message-id",
                defaultValue: "F7916D36-4D5F-4A64-BD08-644E8A23AAAA",
                validators: [notNull(), isNumeric()],
              },
            ],
          },
        },
        {
          testName: "Update Report",
          testDescription:
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis a porta sem, at posuere velit. Donec tempor non ligula et congue. Donec faucibus lobortis lorem, et luctus lacus efficitur at. ",
          template: require("./create-report-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
              {
                label: "Url",
                id: "url",
                defaultValue: "http://localhost:8081/report",
                validators: [isUrl(), isReportUrl()],
              },
              {
                label: "Content Type",
                id: "content-type",
                defaultValue: "application/xml",
                validators: [notNull(), minLength(7)],
              },
            ],
            [RequestHeaderProps.Body]: [
              {
                label: "First Name",
                id: "first-name",
                defaultValue: "John",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Last Name",
                id: "last-name",
                defaultValue: "Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Message ID",
                id: "message-id",
                defaultValue: "F7916D36-4D5F-4A64-BD08-644E8A23AAAA",
                validators: [notNull(), isNumeric()],
              },
            ],
          },
        },
      ],
    },
  ],
};

export default schema;
