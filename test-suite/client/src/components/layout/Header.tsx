import React from "react";
import { Header as NhsHeader } from "nhsuk-react-components";
import NavBar from "./NavBar";

type Props = {
  hideNav?: Boolean;
};
const Header = ({ hideNav }: Props) => {
  return (
    <NhsHeader transactional>
      <NhsHeader.Container>
        <NhsHeader.Logo href="/" />
        <NhsHeader.ServiceName href="/">
          111 Adaptor Test Suite
        </NhsHeader.ServiceName>
        <NhsHeader.Content>
          <NhsHeader.MenuToggle />
        </NhsHeader.Content>
      </NhsHeader.Container>
      {!hideNav && <NavBar />}
    </NhsHeader>
  );
};

export default Header;
