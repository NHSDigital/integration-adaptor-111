import { RequestHeaderProps, Schema } from "../types";
import {
  isLength,
  isAlpha,
  isNumeric,
  isReportUrl,
  minLength,
  notNull,
  isUrl,
  isAlphanumeric,
} from "../utils/validators";

const datePlaceholder = "YYYYMMDDhhmm";

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
          testName: "Patient Referred to Primary Care for Assessment",
          testDescription:
            "111 Report indicating that a call has resulted in the patient being referred to GP for assessment.",
          template: require("./primary-emergency-itk-request.xml"),
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
                label: "ODS Code",
                id: "ods-code",
                defaultValue: "EM396",
                validators: [notNull()],
              },
              {
                label: "DOS Code",
                id: "dos-code",
                defaultValue: "26428",
                validators: [],
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Recipient Name",
                id: "recipent-name",
                defaultValue: "John Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Recipient Address Line 1",
                id: "recipent-street-address-line-1",
                defaultValue: "99 Made up Address",
                validators: [notNull()],
              },
              {
                label: "Recipient Address Line 2",
                id: "recipent-street-address-line-2",
                defaultValue: "Made up Street",
                validators: [notNull()],
              },
              {
                label: "Recipient Town",
                id: "recipent-street-address-town",
                defaultValue: "Made up Town",
                validators: [],
              },
              {
                label: "Recipient City",
                id: "recipent-street-address-city",
                defaultValue: "Made up City",
                validators: [],
              },
              {
                label: "Recipient Postcode",
                id: "recipent-street-address-postcode",
                defaultValue: "M27 1XR",
                validators: [notNull()],
              },
              {
                label: "Encounter Start Datetime",
                id: "nhs111-encounter-date-time-start",
                defaultValue: "202201061200",
                validators: [notNull()],
              },
              {
                label: "Encounter End Datetime",
                id: "nhs111-encounter-date-time-end",
                defaultValue: "2022010613",
                validators: [notNull()],
              },
              {
                label: "Disposition Code",
                id: "disposition-code",
                defaultValue: "Dx001",
                validators: [notNull(), minLength(4)],
              },
              {
                label: "Disposition Text",
                id: "disposition-text",
                defaultValue: "Passed onto clinical hub",
                validators: [notNull()],
              },
            ],
          },
        },
        {
          testName: "Safeguarding Referral",
          testDescription:
            "This test will attempt to send notification of a safeguarding referral to Social Services.",
          template: require("./safeguarding-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
              {
                label: "url",
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
                label: "ODS Code",
                id: "ods-code",
                defaultValue: "EM396",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "DOS Code",
                id: "dos-code",
                defaultValue: "26428",
                validators: [isAlphanumeric()],
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1717636608",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Recipient Name",
                id: "recipient-name",
                defaultValue: "John Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Recipient Address Line 1",
                id: "recipient-street-address-line-1",
                defaultValue: "1 Albion Place",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Recipient Address Line 2",
                id: "recipient-street-address-line-2",
                defaultValue: "Leeds City Centre",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Recipient Town",
                id: "recipient-street-address-town",
                defaultValue: "",
                validators: [isAlpha()],
              },
              {
                label: "Recipient City",
                id: "recipient-street-address-city",
                defaultValue: "Leeds",
                validators: [isAlpha()],
              },
              {
                label: "Recipient Postcode",
                id: "recipient-street-address-postcode",
                defaultValue: "LS1 6LJ",
                validators: [isAlphanumeric()],
              },
              {
                label: "Encounter Start Datetime",
                id: "nhs111-encounter-date-time-start",
                defaultValue: "202205311539",
                validators: [notNull(), isNumeric(), isLength(12)],
                placeholder: datePlaceholder,
              },
              {
                label: "Encounter End Datetime",
                id: "nhs111-encounter-date-time-end",
                defaultValue: "202205311615",
                validators: [notNull(), isNumeric(), isLength(12)],
                placeholder: datePlaceholder,
              },
            ],
          },
        },
        {
          testName: "Patient sent to A&E",
          testDescription:
            "Patient referred to A&E  [e.g. Dx02 - Attend Emergency Treatment Centre within 1 hour]",
          template: require("./primary-to-a&e-request.xml"),
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
                label: "ODS Code",
                id: "ods-code",
                defaultValue: "EM396",
                validators: [notNull()],
              },
              {
                label: "DOS Code",
                id: "dos-code",
                defaultValue: "26428",
                validators: [],
              },
              {
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Recipient Name",
                id: "recipent-name",
                defaultValue: "John Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Recipient Address Line 1",
                id: "recipent-street-address-line-1",
                defaultValue: "99 Made up Address",
                validators: [notNull()],
              },
              {
                label: "Recipient Address Line 2",
                id: "recipent-street-address-line-2",
                defaultValue: "Made up Street",
                validators: [notNull()],
              },
              {
                label: "Recipient Town",
                id: "recipent-street-address-town",
                defaultValue: "Made up Town",
                validators: [],
              },
              {
                label: "Recipient City",
                id: "recipent-street-address-city",
                defaultValue: "Made up City",
                validators: [],
              },
              {
                label: "Recipient Postcode",
                id: "recipent-street-address-postcode",
                defaultValue: "M27 1XR",
                validators: [notNull()],
              },
              {
                label: "Encounter Start Datetime",
                id: "nhs111-encounter-date-time-start",
                defaultValue: "202201061200",
                validators: [notNull(), isNumeric()],
                placeholder: datePlaceholder,
              },
              {
                label: "Encounter End Datetime",
                id: "nhs111-encounter-date-time-end",
                defaultValue: "2022010613",
                validators: [notNull(), isNumeric()],
                placeholder: datePlaceholder,
              },
            ],
          },
        },
        {
          testName: "Primary Care Referral: Two Locations - Scenario 2",
          testDescription:
            "This test attempts to send notification of a primary care referral where the GP surgery has two locations",
          template: require("./primary-care-itk-two-locations.xml"),
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
                label: "NHS Number",
                id: "nhs-number",
                defaultValue: "1234567891",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Recipient ODS Code",
                id: "ods-code",
                defaultValue: "E88122002",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Recipient DOS Code",
                id: "dos-code",
                defaultValue: "159744",
                validators: [isAlphanumeric()],
              },
              {
                label: "Recipient Name",
                id: "recipient-name",
                defaultValue: "Bramley Road Surgery",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Recipient Address Line 1",
                id: "recipient-street-address-line-1",
                defaultValue: "Bramley Road Surgery",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Recipient Address Line 2",
                id: "recipient-street-address-line-2",
                defaultValue: "2 Bramley Road",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Recipient Town",
                id: "recipient-street-address-town",
                defaultValue: "Ealing",
                validators: [isAlpha()],
              },
              {
                label: "Recipient City",
                id: "recipient-street-address-city",
                defaultValue: "London",
                validators: [isAlpha()],
              },
              {
                label: "Recipient Postcode",
                id: "recipient-street-address-postcode",
                defaultValue: "W5 4SS",
                validators: [isAlphanumeric()],
              },
              {
                label: "Backup Recipient ODS Code",
                id: "ods-code-2",
                defaultValue: "E88122",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Backup Recipient DOS Code",
                id: "dos-code-2",
                defaultValue: "161145",
                validators: [isAlphanumeric()],
              },
              {
                label: "Backup Recipient Name",
                id: "recipient-name-2",
                defaultValue: "Florence Road Surgery",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Backup Recipient Address Line 1",
                id: "recipient-street-address-line-1-2",
                defaultValue: "Florence Road Surgery",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Backup Recipient Address Line 2",
                id: "recipient-street-address-line-2-2",
                defaultValue: "26 Florence Road",
                validators: [notNull(), isAlphanumeric()],
              },
              {
                label: "Backup Recipient Town",
                id: "recipient-street-address-town-2",
                defaultValue: "Ealing",
                validators: [isAlpha()],
              },
              {
                label: "Backup Recipient City",
                id: "recipient-street-address-city-2",
                defaultValue: "London",
                validators: [isAlpha()],
              },
              {
                label: "Backup Recipient Postcode",
                id: "recipient-street-address-postcode-2",
                defaultValue: "W5 3TX",
                validators: [isAlphanumeric()],
              },
              {
                label: "Encounter Start Datetime",
                id: "nhs111-encounter-date-time-start",
                defaultValue: "202206101312",
                validators: [notNull(), isNumeric()],
                placeholder: datePlaceholder,
              },
              {
                label: "Encounter End Datetime",
                id: "nhs111-encounter-date-time-end",
                validators: [notNull(), isNumeric()],
                defaultValue: "202206101430",
                placeholder: datePlaceholder,
              },
              {
                label: "Disposition Code",
                id: "disposition-code",
                defaultValue: "12345",
                validators: [notNull(), minLength(4)],
              },
              {
                label: "Disposition Text",
                id: "disposition-text",
                defaultValue: "TEST TEXT",
                validators: [notNull()],
              },
            ],
          },
        },
      ],
    },
  ],
};

export default schema;
