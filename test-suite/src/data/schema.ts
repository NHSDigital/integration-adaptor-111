import { RequestHeaderProps, Schema } from "../types";
import {
  isLength,
  isAlpha,
  isLocalUrl,
  isNumeric,
  isReportUrl,
  minLength,
  notNull,
} from "../utils/validators";

const schema: Schema = {
  testGroups: [
    {
      groupName: "Report",
      testList: [
        {
          testName: "Create Report",
          testDescription:
            "This test will attempt to update an emergency request with adaptor... lorumlorumlorum",
          template: require("./create-report-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
              {
                label: "Url",
                id: "url",
                defaultValue: "http://localhost:8081/report",
                validators: [isLocalUrl(), isReportUrl()],
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
                id: "firstName",
                defaultValue: "John",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Last Name",
                id: "lastName",
                defaultValue: "Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "NHS Number",
                id: "nhsNumber",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Message ID",
                id: "messageId",
                defaultValue: "F7916D36-4D5F-4A64-BD08-644E8A23AAAA",
                validators: [notNull(), isNumeric()],
              },
            ],
          },
        },
        {
          testName: "Update Report",
          testDescription:
            "This test will attempt to update an emergency request with adaptor... lorumlorumlorum",
          template: require("./create-report-request.xml"),
          testSpecifications: {
            [RequestHeaderProps.Header]: [
              {
                label: "Url",
                id: "url",
                defaultValue: "http://localhost:8081/report",
                validators: [isLocalUrl(), isReportUrl()],
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
                id: "firstName",
                defaultValue: "John",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "Last Name",
                id: "lastName",
                defaultValue: "Stones",
                validators: [notNull(), isAlpha()],
              },
              {
                label: "NHS Number",
                id: "nhsNumber",
                defaultValue: "1112223344",
                validators: [notNull(), isNumeric(), isLength(10)],
              },
              {
                label: "Message ID",
                id: "messageId",
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
