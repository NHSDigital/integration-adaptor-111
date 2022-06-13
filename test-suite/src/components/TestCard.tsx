import React from "react";
import { Card, Details } from "nhsuk-react-components";
import RequestForm from "./RequestForm";
import { TestRequestField, Test, SslCerts } from "../types";
import replaceSpaces from "../utils/replaceSpaces";

interface Props {
  test: Test;
  globals: Array<TestRequestField>;
  sslCerts: SslCerts;
}

const TestCard = ({ test, globals, sslCerts }: Props) => {
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
              sslCerts={sslCerts}
            />
          </Details.Text>
        </Details>
      </Card.Content>
    </Card>
  );
};

export default TestCard;
