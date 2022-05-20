import React, { Fragment } from "react";
import { Container } from "nhsuk-react-components";
import Footer from "./Footer";
import Header from "./Header";
import Main from "./Main";

type Props = {
  children: React.ReactNode;
  hideNav?: Boolean;
  hideFooter?: Boolean;
};

const Layout = ({ children, hideNav, hideFooter }: Props) => {
  return (
    <Fragment>
      <Header hideNav={hideNav} />
      <Main>
        <Container>{children}</Container>
      </Main>
      {!hideFooter && <Footer />}
    </Fragment>
  );
};

export default Layout;
