import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Input, Row } from "nhsuk-react-components";
import {
  AdaptorRequest,
  FormError,
  FormErrors,
  RequestBody,
  RequestHeaders,
  TestRequestField,
  TestSpecs,
} from "../types";
import createDefaultRequest from "../utils/createDefaultRequest";
import createFormErrors from "../utils/createFormErrors";
import sendXmlRequest, { AdaptorResponse } from "../utils/sendXmlRequest";

type Props = {
  name: string;
  specs: TestSpecs;
  template: string;
};

const RequestForm = ({ name, specs, template }: Props) => {
  const [form, setForm] = useState<AdaptorRequest>(createDefaultRequest(specs));
  const [errors, setErrors] = useState<FormErrors>(createFormErrors(specs));
  const [response, setResponse] = useState<AdaptorResponse | null>(null);

  const specEntries = Object.entries(specs);

  const onReset = () => {
    setForm(createDefaultRequest(specs));
    setErrors(createFormErrors(specs));
    setResponse(null);
  };

  const onSubmit = async () => {
    const response = await sendXmlRequest(form, template);
    setResponse(response);
  };

  const validateField = (field: string, value: string) => {
    let validatedErrors = errors;
    const fieldValidation = validatedErrors[field];
    if (fieldValidation) {
      validatedErrors[field] = Object.entries(fieldValidation).reduce(
        (acc, [k, v]) => {
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
            isError =
              v.match instanceof RegExp && !new RegExp(v.match).test(value);
          }
          return {
            ...acc,
            [k]: {
              ...v,
              error: isError,
            },
          };
        },
        {} as FormError
      );
    }
    setErrors(validatedErrors);
  };

  const inputError = (field: string) => {
    const fieldValidation = errors[field];
    const errorMessage =
      !!fieldValidation &&
      Object.entries(fieldValidation)
        .sort((x, y) => y[1].precedence - x[1].precedence)
        .reduce(
          (acc, [k, v]) => (v.error ? v.message : acc),
          undefined as undefined | string
        );
    return errorMessage;
  };

  return (
    <Card>
      <Card.Content>
        {specEntries.map(
          ([k, v]: [string, Array<TestRequestField>], i) =>
            Array.isArray(v) && (
              <Row key={"K-" + name + k}>
                {v.map((f: TestRequestField) => {
                  const key = k as keyof AdaptorRequest;
                  const field = f.id as keyof (RequestBody | RequestHeaders);
                  return (
                    <Col width="one-half" key={"K-" + name + key + field}>
                      <Input
                        id={f.id}
                        name={f.id}
                        label={f.label}
                        value={form[key][field]}
                        error={inputError(field)}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                          validateField(f.id, e.target.value);
                          setForm({
                            ...form,
                            [key]: {
                              ...form[key],
                              [field]: e.target.value,
                            },
                          });
                        }}
                      />
                    </Col>
                  );
                })}
                {i === specEntries.length - 1 && (
                  <Col
                    width="full"
                    style={{
                      display: "flex",
                      alignItems: "end",
                      justifyContent: "space-between",
                    }}
                  >
                    <Button
                      secondary
                      style={{ marginRight: "36px" }}
                      onClick={onReset}
                    >
                      Reset
                    </Button>
                    <Button onClick={onSubmit}>Send</Button>
                  </Col>
                )}
              </Row>
            )
        )}
        {response && (
          <Card>
            <Card.Content>
              <pre>Response Status: {response.status}</pre>
              <pre
                style={{
                  overflow: "scroll",
                  maxHeight: "450px",
                }}
              >
                {response.xml}
              </pre>
            </Card.Content>
          </Card>
        )}
      </Card.Content>
    </Card>
  );
};

export default RequestForm;
