import React from "react";
import { Card, Details } from "nhsuk-react-components";
import RequestForm from "./RequestForm";
import { Test } from "../types";

interface Props {
  test: Test;
}

const TestCard = ({ test }: Props) => {
  return (
    <Card feature>
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
            />
          </Details.Text>
        </Details>
      </Card.Content>
    </Card>
  );
};

export default TestCard;
