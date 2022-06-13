import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Input, Row } from "nhsuk-react-components";
import {
  AdaptorRequest,
  AdaptorResponse,
  FormErrors,
  TestRequestField,
  TestSpecs,
} from "../types";
import createDefaultRequest from "../utils/createDefaultRequest";
import { createRequestErrors } from "../utils/createFormErrors";
import { validateField, validateForm } from "../utils/validators";
import { serverUrl } from "../data/schema";
const beautify = require("xml-beautifier");

type Props = {
  name: string;
  specs: TestSpecs;
  template: string;
  globals: Array<TestRequestField>;
  sslCert: File | null;
};

const RequestForm = ({ name, specs, template, globals, sslCert }: Props) => {
  const defaultForm = createDefaultRequest(specs, globals);
  const defaultErrors = validateForm(defaultForm, createRequestErrors(specs));
  const [form, setForm] = useState<AdaptorRequest>(defaultForm);
  const [errors, setErrors] = useState<FormErrors>(defaultErrors);
  const [response, setResponse] = useState<AdaptorResponse | null>(null);
  const specEntries = Object.entries(specs);

  const onReset = () => {
    setForm(defaultForm);
    setErrors(defaultErrors);
    setResponse(null);
  };

  const onSubmit = async () => {
    try {
      console.log(sslCert);
      const reportReq = await fetch(template);
      const xml = await reportReq.text();
      const response = await fetch(serverUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          form,
          template: xml,
          sslCert,
        }),
      }).then((r) => r.json());
      setResponse(response);
    } catch (e) {
      console.error(e);
    }
  };

  const onValidate = (fieldName: string, value: string) => {
    let validatedErrors = errors;
    const rules = validatedErrors[fieldName];
    if (rules) {
      validatedErrors[fieldName] = validateField(rules, value);
      setErrors(validatedErrors);
    }
  };

  const isDisabled = Object.entries(errors).some(([key, rules]) =>
    rules ? Object.entries(rules).some(([k, rule]) => rule.error) : false
  );

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
                {v.map((field: TestRequestField) => {
                  const key = k as keyof AdaptorRequest;
                  return (
                    <Col width="one-half" key={"K-" + name + key + field.id}>
                      <Input
                        id={field.id}
                        name={field.id}
                        label={field.label}
                        placeholder={field.placeholder}
                        value={form[key][field.id]}
                        error={inputError(field.id)}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                          onValidate(field.id, e.target.value);
                          setForm({
                            ...form,
                            [key]: {
                              ...form[key],
                              [field.id]: e.target.value,
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
                    <Button disabled={isDisabled} onClick={onSubmit}>
                      Send
                    </Button>
                  </Col>
                )}
              </Row>
            )
        )}
        {response && (
          <Card>
            <Card.Content>
              <pre>Response Status: {response.adaptorStatus}</pre>
              <pre
                style={{
                  overflow: "scroll",
                  maxHeight: "450px",
                }}
              >
                {beautify(response.adaptorResponse)}
              </pre>
            </Card.Content>
          </Card>
        )}
      </Card.Content>
    </Card>
  );
};

export default RequestForm;
