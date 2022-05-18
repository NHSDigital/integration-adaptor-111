import React, { useState } from "react";
import { Header as NhsHeader } from "nhsuk-react-components";
import { routes } from "../../routes";
import { AppRoute } from "../../types";

const NavBar = () => {
  const [navItemHovered, setNavItemHovered] = useState<String | null>();

  return (
    <NhsHeader.Nav>
      {routes
        .filter((r: AppRoute) => r.nav)
        .map((r: AppRoute) => (
          <NhsHeader.NavItem
            key={"K-" + r.path}
            href={r.path}
            onMouseEnter={() => setNavItemHovered(r.path)}
            onMouseLeave={() => setNavItemHovered(null)}
            style={{
              textDecoration: navItemHovered === r.path ? "none" : "underline",
            }}
          >
            {r.name}
          </NhsHeader.NavItem>
        ))}
    </NhsHeader.Nav>
  );
};

export default NavBar;
