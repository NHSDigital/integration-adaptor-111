import React, { ChangeEvent, useState } from "react";
import { Button, Card, Col, Details, Input, Row } from "nhsuk-react-components";
import {
  CertificateTypes,
  FormErrors,
  SslCerts,
  TestRequestField
} from "../types";
import globalVars from "../data/globals";
import { createGlobalErrors } from "../utils/createFormErrors";
import { validateField } from "../utils/validators";

type Props = {
  setGlobals: (state: Array<TestRequestField>) => void;
  globals: Array<TestRequestField>;
  defaultGlobals: Array<TestRequestField>;
  sslCerts: SslCerts;
  setSslCerts: (certs: SslCerts) => void;
};

const GlobalsForm = ({
  setGlobals,
  globals,
  defaultGlobals,
  sslCerts,
  setSslCerts
}: Props) => {
  const [errors, setErrors] = useState<FormErrors>(
    createGlobalErrors(globalVars)
  );
  const [showPassword, setShowPassword] = useState(false);

  const onReset = () => {
    setErrors(createGlobalErrors(globalVars));
    setGlobals(defaultGlobals);
    setSslCerts({
      ca: null,
      key: null,
      p12: null,
      password: ""
    });
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
      const fileType = e.target.getAttribute("accept");
      if (fileType === CertificateTypes.KEY) {
        setSslCerts({
          ...sslCerts,
          key: file
        });
      } else if (fileType === CertificateTypes.P12) {
        setSslCerts({
          ...sslCerts,
          p12: file
        });
      } else if (fileType === CertificateTypes.CA) {
        setSslCerts({
          ...sslCerts,
          ca: file
        });
      }
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
                            gbl.id === field.id ? e.target.value : gbl.value
                        }))
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
                      defaultValue={sslCerts.ca ? sslCerts.ca.name : ""}
                      disabled
                    />
                  </Col>
                  <Col width="one-third">
                    <input
                      type="file"
                      accept={CertificateTypes.CA}
                      style={{ display: "none" }}
                      id="cer-file-input"
                      name="sslCert"
                      onChange={onFileAdd}
                    />
                    {sslCerts.ca ? (
                      <Button
                        style={{
                          margin: "12px 0 0 0",
                          background: "rgba(153, 0, 0, 0.7)"
                        }}
                        onClick={() =>
                          setSslCerts({
                            ...sslCerts,
                            ca: null
                          })
                        }
                      >
                        X
                      </Button>
                    ) : (
                      <label htmlFor="cer-file-input">
                        <Button
                          as="a"
                          style={{
                            margin: "12px 0 0 0"
                          }}
                        >
                          Attach
                        </Button>
                      </label>
                    )}
                  </Col>
                </Row>
              </Col>
              <Col width="one-half">
                <Row>
                  <Col width="two-thirds">
                    <Input
                      name="CA Key"
                      label="CA Key"
                      defaultValue={sslCerts.key ? sslCerts.key.name : ""}
                      disabled
                    />
                  </Col>
                  <Col width="one-third">
                    <input
                      type="file"
                      accept={CertificateTypes.KEY}
                      style={{ display: "none" }}
                      id="key-file-input"
                      name="sslKey"
                      onChange={onFileAdd}
                    />
                    {sslCerts.key ? (
                      <Button
                        style={{
                          margin: "12px 0 0 0",
                          background: "rgba(153, 0, 0, 0.7)"
                        }}
                        onClick={() =>
                          setSslCerts({
                            ...sslCerts,
                            key: null
                          })
                        }
                      >
                        X
                      </Button>
                    ) : (
                      <label htmlFor="key-file-input">
                        <Button
                          as="a"
                          style={{
                            margin: "12px 0 0 0"
                          }}
                        >
                          Attach
                        </Button>
                      </label>
                    )}
                  </Col>
                </Row>
              </Col>
              <Col width="one-half">
                <Row>
                  <Col width="two-thirds">
                    <Input
                      name="P12 Certificate"
                      label="P12 Certificate"
                      defaultValue={sslCerts.p12 ? sslCerts.p12.name : ""}
                      disabled
                    />
                  </Col>
                  <Col width="one-third">
                    <input
                      type="file"
                      accept={CertificateTypes.P12}
                      style={{ display: "none" }}
                      id="p12-file-input"
                      name="p12Cert"
                      onChange={onFileAdd}
                    />
                    {sslCerts.p12 ? (
                      <Button
                        style={{
                          margin: "12px 0 0 0",
                          background: "rgba(153, 0, 0, 0.7)"
                        }}
                        onClick={() =>
                          setSslCerts({
                            ...sslCerts,
                            p12: null
                          })
                        }
                      >
                        X
                      </Button>
                    ) : (
                      <label htmlFor="p12-file-input">
                        <Button
                          as="a"
                          style={{
                            margin: "12px 0 0 0"
                          }}
                        >
                          Attach
                        </Button>
                      </label>
                    )}
                  </Col>
                </Row>
              </Col>
              <Col width="one-half">
                <Row>
                  <Col width="two-thirds">
                    <Input
                      name="Password"
                      label="Password"
                      type={showPassword ? "text" : "password"}
                      onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        setSslCerts({
                          ...sslCerts,
                          password: e.target.value
                        })
                      }
                      value={sslCerts.password ? sslCerts.password : ""}
                    />
                  </Col>
                  <Col width="one-third">
                    <Button
                      onClick={() => setShowPassword(!showPassword)}
                      style={{
                        margin: "12px 0 0 0"
                      }}
                    >
                      {showPassword ? "Hide" : "Show"}
                    </Button>
                  </Col>
                </Row>
              </Col>
              <Col
                width="full"
                style={{
                  display: "flex",
                  alignItems: "end",
                  justifyContent: "space-between"
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
            <Card.Description>Certificates for SSL connection </Card.Description>
          </Card.Content>
        </Card>
      </Details.Text>
    </Details>
  );
};

export default GlobalsForm;
