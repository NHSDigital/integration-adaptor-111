import { Button, Card, Col, Details, Input, Row } from "nhsuk-react-components";
import React, { ChangeEvent } from "react";
import { TestRequestField } from "../types";

type Props = {
  setGlobals: (state: Array<TestRequestField>) => void;
  globals: Array<TestRequestField>;
};

const GlobalsForm = ({ setGlobals, globals }: Props) => (
  <Details expander>
    <Details.Summary>Set globals</Details.Summary>
    <Details.Text>
      <Card>
        <Card.Content>
          <Row>
            {globals.map((g) => (
              <Col width="one-half" key={"G-" + g.label}>
                <Input
                  id={g.id}
                  name={g.id}
                  label={g.label}
                  value={g.value}
                  onChange={(e: ChangeEvent<HTMLInputElement>) => {
                    const field = globals.find((gl) => gl.id === g.id);
                    if (field) {
                      setGlobals([
                        ...globals.map((gbl) => ({
                          ...gbl,
                          value:
                            gbl.id === field.id ? e.target.value : gbl.value,
                        })),
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
                justifyContent: "space-between",
              }}
            >
              <Button
                secondary
                style={{ marginRight: "36px" }}
                onClick={() =>
                  setGlobals(
                    globals.map((g) => ({
                      ...g,
                      value: g.defaultValue,
                    }))
                  )
                }
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

export default GlobalsForm;
