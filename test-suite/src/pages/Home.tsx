import React, { useState } from "react";
import { Col, Row } from "nhsuk-react-components";
import Layout from "../components/layout";
import TestCard from "../components/TestCard";
import schema from "../data/schema";
import gbl from "../data/globals";
import { TestRequestField, Test, Certificate, SslCerts } from "../types";
import GlobalsForm from "../components/GlobalsForm";
const defaultGlobals = gbl.map((g) => ({
  ...g,
  value: g.defaultValue,
}));

const Home = () => {
  const [globals, setGlobals] =
    useState<Array<TestRequestField>>(defaultGlobals);

  const [sslCerts, setSslCerts] = useState<SslCerts>({
    ca: null,
    key: null,
    p12: null,
    password: "",
  });

  return (
    <Layout>
      <GlobalsForm
        setGlobals={setGlobals}
        globals={globals}
        defaultGlobals={defaultGlobals}
        sslCerts={sslCerts}
        setSslCerts={setSslCerts}
      />
      {schema.testGroups.map(({ testList, groupName }) => (
        <Row key={"K-" + groupName}>
          {testList.map((t: Test) => {
            return (
              <Col key={"K-" + t.testName} width="full">
                <TestCard test={t} globals={globals} sslCerts={sslCerts} />
              </Col>
            );
          })}
        </Row>
      ))}
    </Layout>
  );
};

export default Home;
