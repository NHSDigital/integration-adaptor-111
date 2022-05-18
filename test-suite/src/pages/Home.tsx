import React from "react";
import { Col, Row } from "nhsuk-react-components";
import Layout from "../components/layout";
import TestCard from "../components/TestCard";
import schema from "../data/schema";
import { Test } from "../types";

const Home = () => {
  return (
    <Layout>
      {schema.testGroups.map(({ testList, groupName }) => (
        <Row key={"K-" + groupName}>
          {testList.map((t: Test) => (
            <Col key={"K-" + t.testName} width="full">
              <TestCard test={t} />
            </Col>
          ))}
        </Row>
      ))}
    </Layout>
  );
};

export default Home;
