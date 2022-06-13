import React, { useState } from "react";
import { Col, Row } from "nhsuk-react-components";
import Layout from "../components/layout";
import TestCard from "../components/TestCard";
import schema from "../data/schema";
import gbl from "../data/globals";
import { TestRequestField, Test, Certificate } from "../types";
import GlobalsForm from "../components/GlobalsForm";
const defaultGlobals = gbl.map((g) => ({
  ...g,
  value: g.defaultValue,
}));

const Home = () => {
  const [globals, setGlobals] =
    useState<Array<TestRequestField>>(defaultGlobals);

  const [sslCert, setSslCert] = useState<File | null>(null);
  const [p12Cert, setP12Cert] = useState<File | null>(null);

  return (
    <Layout>
      <GlobalsForm
        setGlobals={setGlobals}
        globals={globals}
        defaultGlobals={defaultGlobals}
        sslCert={sslCert}
        setSslCert={setSslCert}
      />
      {schema.testGroups.map(({ testList, groupName }) => (
        <Row key={"K-" + groupName}>
          {testList.map((t: Test) => {
            return (
              <Col key={"K-" + t.testName} width="full">
                <TestCard test={t} globals={globals} sslCert={sslCert} />
              </Col>
            );
          })}
        </Row>
      ))}
    </Layout>
  );
};

export default Home;
