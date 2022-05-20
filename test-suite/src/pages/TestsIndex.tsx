import React from "react";
import Layout from "../components/layout";
import schema from "../data/schema";
import { ListPanel } from "nhsuk-react-components";
import { Test, TestIndex } from "../types";

const testList = schema.testGroups.reduce(
  (acc, { testList }) => [...acc, ...testList],
  [] as Array<Test>
);

const testIndex: TestIndex = testList.reduce((acc: TestIndex, val: Test) => {
  const key: string = val.testName.substring(0, 1);
  acc = {
    ...acc,
    [key]: key in acc ? [...acc[key], val.testName] : [val.testName],
  };
  return acc;
}, {} as TestIndex);

const TestsIndex = () => {
  return (
    <Layout>
      <ListPanel>
        {Object.entries(testIndex).map(([k, v]: [string, Array<string>]) => (
          <ListPanel.Panel
            label={k}
            key={"K-" + k}
            labelProps={{ id: k }}
            backToTop
            backToTopLink="#"
          >
            {v.map((name) => (
              <ListPanel.LinkItem
                key={"K-" + name}
                href="/conditions/abdominal-aortic-aneurysm/"
              >
                {name}
              </ListPanel.LinkItem>
            ))}
          </ListPanel.Panel>
        ))}
      </ListPanel>
    </Layout>
  );
};

export default TestsIndex;
