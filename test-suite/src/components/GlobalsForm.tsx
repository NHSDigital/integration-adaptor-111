import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Details, Input, Row } from "nhsuk-react-components";
import { Certificate, FormErrors, TestRequestField } from "../types";
import globalVars from "../data/globals";
import { createGlobalErrors } from "../utils/createFormErrors";
import { validateField } from "../utils/validators";
import readTextFile from "../utils/readTextFile";

type Props = {
  setGlobals: (state: Array<TestRequestField>) => void;
  globals: Array<TestRequestField>;
  defaultGlobals: Array<TestRequestField>;
  sslCert: File | null;
  setSslCert: (cert: File | null) => void;
};

enum CertificateTypes {
  CER = ".cer",
  P12 = ".p12",
}

const GlobalsForm = ({
  setGlobals,
  globals,
  defaultGlobals,
  sslCert,
  setSslCert,
}: Props) => {
  const [errors, setErrors] = useState<FormErrors>(
    createGlobalErrors(globalVars)
  );

  const onReset = () => {
    setErrors(createGlobalErrors(globalVars));
    setGlobals(defaultGlobals);
    setSslCert(null);
    // setP12Cert(null);
  };

  const onValidate = (field: string, value: string) => {
    let validatedErrors = errors;
    const fieldValidation = validatedErrors[field];
    if (fieldValidation) {
      validatedErrors[field] = validateField(fieldValidation, value);
      setErrors(validatedErrors);
    }
  };

  const onFileAdd = async (e: ChangeEvent<HTMLInputElement>) => {
    const { files } = e.target;
    if (files && files.length) {
      const file = files[0];
      setSslCert(file);
    }
    e.target.value = "";
    e.target.files = null;
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
    <Details expander>
      <Details.Summary>Set globals</Details.Summary>
      <Details.Text>
        <Card>
          <Card.Content>
            <Row>
              {globals.map((field) => (
                <Col width="one-half" key={"G-" + field.label}>
                  <Input
                    id={field.id}
                    name={field.id}
                    label={field.label}
                    value={field.value}
                    error={inputError(field.id)}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => {
                      onValidate(field.id, e.target.value);
                      setGlobals([
                        ...globals.map((gbl) => ({
                          ...gbl,
                          value:
                            gbl.id === field.id ? e.target.value : gbl.value,
                        })),
                      ]);
                    }}
                  />
                </Col>
              ))}
              <Col width="one-half">
                <Row>
                  <Col width="two-thirds">
                    <Input
                      name="CA Certificate"
                      label="CA Certificate"
                      defaultValue={sslCert ? sslCert.name : ""}
                      disabled
                    />
                  </Col>
                  <Col width="one-third">
                    <input
                      type="file"
                      accept={CertificateTypes.CER}
                      style={{ display: "none" }}
                      id="cer-file-input"
                      onChange={onFileAdd}
                    />
                    {sslCert ? (
                      <Button
                        style={{
                          margin: "12px 0 0 0",
                          background: "rgba(153, 0, 0, 0.7)",
                        }}
                        onClick={() => setSslCert(null)}
                      >
                        X
                      </Button>
                    ) : (
                      <label htmlFor="cer-file-input">
                        <Button
                          as="a"
                          style={{
                            margin: "12px 0 0 0",
                          }}
                        >
                          Attach
                        </Button>
                      </label>
                    )}
                  </Col>
                </Row>
              </Col>
              {/* <Col width="one-half">
                <Row>
                  <Col width="two-thirds">
                    <Input
                      name="P12 Certificate"
                      label="P12 Certificate"
                      defaultValue={p12Cert ? p12Cert.name : ""}
                      disabled
                    />
                  </Col>
                  <Col width="one-third">
                    <input
                      type="file"
                      accept={CertificateTypes.P12}
                      style={{ display: "none" }}
                      id="p12-file-input"
                      onChange={onFileAdd}
                    />
                    {p12Cert ? (
                      <Button
                        style={{
                          margin: "12px 0 0 0",
                          background: "rgba(153, 0, 0, 0.7)",
                        }}
                        onClick={() => setP12Cert(null)}
                      >
                        X
                      </Button>
                    ) : (
                      <label htmlFor="p12-file-input">
                        <Button
                          as="a"
                          style={{
                            margin: "12px 0 0 0",
                          }}
                        >
                          Attach
                        </Button>
                      </label>
                    )}
                  </Col>
                </Row>
              </Col> */}
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
              </Col>
            </Row>
          </Card.Content>
        </Card>
      </Details.Text>
    </Details>
  );
};

export default GlobalsForm;
