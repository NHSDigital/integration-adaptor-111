import React, { useState } from "react";
import { Col, Row } from "nhsuk-react-components";
import Layout from "../components/layout";
import TestCard from "../components/TestCard";
import schema from "../data/schema";
import defaultGlobals from "../data/globals";
import { TestRequestField, Test } from "../types";
import GlobalsForm from "../components/GlobalsForm";

const Home = () => {
  const [globals, setGlobals] = useState<Array<TestRequestField>>(
    defaultGlobals.map((g) => ({
      ...g,
      value: g.defaultValue
    }))
  );

  return (
    <Layout>
      <GlobalsForm setGlobals={setGlobals} globals={globals} />
      {schema.testGroups.map(({ testList, groupName }) => (
        <Row key={"K-" + groupName}>
          {testList.map((t: Test) => {
            return (
              <Col key={"K-" + t.testName} width="full">
                <TestCard test={t} globals={globals} />
              </Col>
            );
          })}
        </Row>
      ))}
    </Layout>
  );
};

export default Home;
