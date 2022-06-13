import React from "react";
import { Card, Details } from "nhsuk-react-components";
import RequestForm from "./RequestForm";
import { TestRequestField, Test } from "../types";
import replaceSpaces from "../utils/replaceSpaces";

interface Props {
  test: Test;
  globals: Array<TestRequestField>;
  sslCert: File | null;
}

const TestCard = ({ test, globals, sslCert }: Props) => {
  return (
    <Card feature id={replaceSpaces(test.testName)}>
      <Card.Content>
        <Card.Heading>{test.testName}</Card.Heading>
        <Card.Description>{test.testDescription}</Card.Description>
        <Details expander>
          <Details.Summary>Run test</Details.Summary>
          <Details.Text>
            <RequestForm
              specs={test.testSpecifications}
              name={test.testName}
              template={test.template}
              globals={globals}
              sslCert={sslCert}
            />
          </Details.Text>
        </Details>
      </Card.Content>
    </Card>
  );
};

export default TestCard;
