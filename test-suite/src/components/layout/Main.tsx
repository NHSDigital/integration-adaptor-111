import React from "react";

const Main = (props: React.HTMLProps<HTMLDivElement>) => {
  return (
    <main
      {...props}
      className="nhsuk-main-wrapper"
      id="maincontent"
      role="main"
    />
  );
};

export default Main;
