import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Input, Row } from "nhsuk-react-components";
import {
  AdaptorRequest,
  AdaptorResponse,
  Form,
  RequestHeaders,
} from "@server/types";
import {
  FormErrors,
  SpecTuple,
  SslCerts,
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
  sslCerts: SslCerts;
};

const RequestForm = ({ name, specs, template, globals, sslCerts }: Props) => {
  const defaultForm = createDefaultRequest(specs, globals);
  const defaultErrors = validateForm(defaultForm, createRequestErrors(specs));
  const [errors, setErrors] = useState<FormErrors>(defaultErrors);
  const [form, setForm] = useState<AdaptorRequest>(defaultForm);
  const [response, setResponse] = useState<AdaptorResponse | null>(null);
  const specEntries: Array<SpecTuple> = Object.entries(specs);

  const onReset = () => {
    setForm(defaultForm);
    setErrors(defaultErrors);
    setResponse(null);
  };

  const onSubmit = async () => {
    try {
      const formData = new FormData();
      const reportReq = await fetch(template);
      const xml = await reportReq.text();
      console.log(sslCerts);
      Object.entries(sslCerts).forEach(([key, file]) => {
        if (file) formData.append(key, file);
      });
      formData.append("form", JSON.stringify(form));
      formData.append("template", xml);

      const response = await fetch(serverUrl + "/report", {
        method: "POST",
        body: formData,
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

  const isDisabled = Object.entries(errors).some(([key, rules]) =>
    rules ? Object.entries(rules).some(([k, rule]) => rule.error) : false
  );

  const isHeaderFields = (
    fields: RequestHeaders | Form
  ): fields is RequestHeaders => (fields as RequestHeaders).url !== undefined;

  return (
    <Card>
      <Card.Content>
        {specEntries.map(
          ([k, v], i) =>
            Array.isArray(v) && (
              <Row key={"K-" + name + k}>
                {v.map((field: TestRequestField) => {
                  const headerKey = k as keyof AdaptorRequest;
                  const fields = form[headerKey];
                  const value = isHeaderFields(fields)
                    ? fields[field.id as keyof RequestHeaders]
                    : fields[field.id];
                  return (
                    <Col
                      width="one-half"
                      key={"K-" + name + field.id + field.id}
                    >
                      <Input
                        id={field.id}
                        name={field.id}
                        label={field.label}
                        placeholder={field.placeholder}
                        value={value}
                        error={inputError(field.id)}
                        onChange={(e: ChangeEvent<HTMLInputElement>) => {
                          onValidate(field.id, e.target.value);
                          setForm({
                            ...form,
                            [headerKey]: {
                              ...form[headerKey],
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
              <pre>API Status: {response.apiStatus}</pre>
              <pre>Adaptor Status: {response.adaptorStatus}</pre>

              <pre
                style={{
                  overflow: "scroll",
                  maxHeight: "450px",
                }}
              >
                {beautify(response.message)}
              </pre>
            </Card.Content>
          </Card>
        )}
      </Card.Content>
    </Card>
  );
};

export default RequestForm;
