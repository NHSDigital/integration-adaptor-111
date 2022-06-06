import { RequestHeaderProps, Schema } from "../types";
import {
  isLength,
  isAlpha,
  isNumeric,
  isReportUrl,
  minLength,
  notNull,
  isUrl,
  isAlphanumeric
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
        {
          testName: "Safeguarding Referral",
          testDescription: "This test will attempt to send notification of a safeguarding referral to Social Services.",
          template: require("./safeguarding-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
              {
                label: "url",
                id: "url",
                defaultValue: "http://localhost:8081/report",
                validators: [isUrl(), isReportUrl()]
              },
              {
                label: "Content Type",
                id: "content-type",
                defaultValue: "application/xml",
                validators: [notNull(), minLength(7)]
              }
            ],
            [RequestHeaderProps.Body]: [
              {
                label: "ODS Code",
                id: "ods-code",
                defaultValue: "EM396",
                validators: [notNull(), isAlphanumeric()]
              },
              {
                label: "DOS Code",
                id: "dos-code",
                defaultValue: "26428",
                validators: [isAlphanumeric()]
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1717636608",
                validators: [notNull(), isNumeric(), isLength(10)]
              },
              {
                label: "Recipient Name",
                id: "recipient-name",
                defaultValue: "John Stones",
                validators: [notNull(), isAlpha()]
              },
              {
                label: "Recipient Address Line 1",
                id: "recipient-street-address-line-1",
                defaultValue: "1 Albion Place",
                validators: [notNull(), isAlphanumeric()]
              },
              {
                label: "Recipient Address Line 2",
                id: "recipient-street-address-line-2",
                defaultValue: "Leeds City Centre",
                validators: [notNull(), isAlphanumeric()]
              },
              {
                label: "Recipient Town",
                id: "recipient-street-address-town",
                defaultValue: "",
                validators: [isAlpha()]
              },
              {
                label: "Recipient City",
                id: "recipient-street-address-city",
                defaultValue: "Leeds",
                validators: [isAlpha()]
              },
              {
                label: "Recipient Postcode",
                id: "recipient-street-address-postcode",
                defaultValue: "LS1 6LJ",
                validators: [isAlphanumeric()]
              },
              {
                label: "Encounter Start Datetime",
                id: "nhs111-encounter-date-time-start",
                defaultValue: "202205311539",
                validators: [notNull(), isNumeric(), isLength(12)],
                date: true
              },
              {
                label: "Encounter End Datetime",
                id: "nhs111-encounter-date-time-end",
                defaultValue: "202205311615",
                validators: [notNull(), isNumeric(), isLength(12)],
                date: true
              }
            ]
          }
        }
      ],
    },
  ],
};

export default schema;
