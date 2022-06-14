import React from "react";
import { Footer as NhsFooter } from "nhsuk-react-components";

const Footer = () => {
  return (
    <NhsFooter>
      <NhsFooter.List>
        <NhsFooter.ListItem href="https://www.nhs.uk/nhs-sites/">
          NHS sites
        </NhsFooter.ListItem>
        <NhsFooter.ListItem href="https://www.nhs.uk/about-us/">
          About us
        </NhsFooter.ListItem>
        <NhsFooter.ListItem href="https://www.nhs.uk/contact-us/">
          Contact us
        </NhsFooter.ListItem>
        <NhsFooter.ListItem href="https://www.nhs.uk/about-us/sitemap/">
          Sitemap
        </NhsFooter.ListItem>
        <NhsFooter.ListItem href="https://www.nhs.uk/our-policies/">
          Our policies
        </NhsFooter.ListItem>
      </NhsFooter.List>
      <NhsFooter.Copyright>&copy; Crown copyright</NhsFooter.Copyright>
    </NhsFooter>
  );
};

export default Footer;
