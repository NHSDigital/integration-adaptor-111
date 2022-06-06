import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Input, Row } from "nhsuk-react-components";
import {
  AdaptorRequest,
  FormErrors,
  TestRequestField,
  TestSpecs,
} from "../types";
import createDefaultRequest from "../utils/createDefaultRequest";
import { createRequestErrors } from "../utils/createFormErrors";
import sendXmlRequest, { AdaptorResponse } from "../utils/sendXmlRequest";
import { validateField } from "../utils/validators";

type Props = {
  name: string;
  specs: TestSpecs;
  template: string;
  globals: Array<TestRequestField>;
};

const RequestForm = ({ name, specs, template, globals }: Props) => {
  const [form, setForm] = useState<AdaptorRequest>(
    createDefaultRequest(specs, globals)
  );
  const [errors, setErrors] = useState<FormErrors>(createRequestErrors(specs));
  const [response, setResponse] = useState<AdaptorResponse | null>(null);
  const datePlaceholder: string = "YYYYMMDDhhmm"

  const specEntries = Object.entries(specs);

  const onReset = () => {
    setForm(createDefaultRequest(specs, globals));
    setErrors(createRequestErrors(specs));
    setResponse(null);
  };

  const onSubmit = async () => {
    const response = await sendXmlRequest(form, template);
    setResponse(response);
  };

  const onValidate = (field: string, value: string) => {
    let validatedErrors = errors;
    const fieldValidation = validatedErrors[field];
    if (fieldValidation) {
      validatedErrors[field] = validateField(fieldValidation, value);
      setErrors(validatedErrors);
    }
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

  const textInputElement = (field: TestRequestField, key: keyof AdaptorRequest) => (
      <Input
          id={field.id}
          name={field.id}
          label={field.label}
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
  );

  const dateInputElement = (field: TestRequestField, key: keyof AdaptorRequest) => (
      <Input
          id={field.id}
          name={field.id}
          label={field.label}
          placeholder={datePlaceholder}
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
  )

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
                      <div key={"K-" + name + key + field.id}>
                        {field.date ? (
                            <Col width="one-half">
                              {dateInputElement(field, key)}
                            </Col>
                        ) : (
                            <Col width="one-half">
                              {textInputElement(field, key)}
                            </Col>
                        )}
                      </div>
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
