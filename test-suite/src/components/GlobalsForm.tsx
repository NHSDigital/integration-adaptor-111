import { Button, Card, Col, Details, Input, Row } from "nhsuk-react-components";
import React, { ChangeEvent, useState } from "react";
import { FormErrors, TestRequestField } from "../types";
import globalVars from "../data/globals";
import { createGlobalErrors } from "../utils/createFormErrors";
import { validateField } from "../utils/validators";

type Props = {
  setGlobals: (state: Array<TestRequestField>) => void;
  globals: Array<TestRequestField>;
};

const GlobalsForm = ({ setGlobals, globals }: Props) => {
  const [errors, setErrors] = useState<FormErrors>(
    createGlobalErrors(globalVars)
  );

  const onReset = () => {
    setErrors(createGlobalErrors(globalVars));
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
                      if (field) {
                        onValidate(field.id, e.target.value);
                        setGlobals([
                          ...globals.map((gbl) => ({
                            ...gbl,
                            value:
                              gbl.id === field.id ? e.target.value : gbl.value
                          }))
                        ]);
                      }
                    }}
                  />
                </Col>
              ))}
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
          </Card.Content>
        </Card>
      </Details.Text>
    </Details>
  );
};

export default GlobalsForm;
