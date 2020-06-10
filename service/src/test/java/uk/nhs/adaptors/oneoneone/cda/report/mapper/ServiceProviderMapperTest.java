package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CO;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedCustodian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Custodian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01CustodianOrganization;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Organization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class ServiceProviderMapperTest {

    public static final String CODE = "GP1Z";
    public static final String PROVIDER_NAME = "CLOCKHOUSE_MEDICAL";
    @Mock
    private AddressMapper addressMapper;

    @Mock
    private ContactPointMapper contactPointMapper;

    @InjectMocks
    private ServiceProviderMapper serviceProviderMapper;

    @Mock
    private AD itkAddress;

    @Mock
    private TEL itkTelecom;

    @Mock
    private ON organizationName;

    @Mock
    private Address address;

    @Mock
    private ContactPoint telecom;

    @Test
    public void mapServiceProvider() {
        POCDMT000002UK01Custodian custodian = mock(POCDMT000002UK01Custodian.class);
        POCDMT000002UK01AssignedCustodian assignedCustodian = mock(POCDMT000002UK01AssignedCustodian.class);
        POCDMT000002UK01CustodianOrganization custodianOrganization = mock(POCDMT000002UK01CustodianOrganization.class);

        when(custodian.getAssignedCustodian()).thenReturn(assignedCustodian);
        when(custodian.isSetTypeCode()).thenReturn(true);
        when(custodian.getTypeCode()).thenReturn(CODE);
        when(assignedCustodian.getRepresentedCustodianOrganization()).thenReturn(custodianOrganization);

        when(custodianOrganization.isSetAddr()).thenReturn(true);
        when(custodianOrganization.getAddr()).thenReturn(itkAddress);
        when(custodianOrganization.isSetTelecom()).thenReturn(true);
        when(custodianOrganization.getTelecom()).thenReturn(itkTelecom);
        when(custodianOrganization.isSetName()).thenReturn(true);
        when(custodianOrganization.getName()).thenReturn(organizationName);

        when(addressMapper.mapAddress(any())).thenReturn(address);
        when(contactPointMapper.mapContactPoint(any())).thenReturn(telecom);

        mockOrganizationNameXmlStructure();

        Organization organization = serviceProviderMapper.mapServiceProvider(custodian);

        assertThat(organization.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(organization.getActive()).isEqualTo(true);
        assertThat(organization.getAddressFirstRep()).isEqualTo(address);
        assertThat(organization.getTelecomFirstRep()).isEqualTo(telecom);
        assertThat(organization.getType().get(0).getText()).isEqualTo(CODE);
        assertThat(organization.getName()).isEqualTo(PROVIDER_NAME);
    }

    private void mockOrganizationNameXmlStructure() {
        Node titleNode = mock(Node.class);
        Node titleSubnode = mock(Node.class);
        when(organizationName.getDomNode()).thenReturn(titleNode);
        when(titleNode.getFirstChild()).thenReturn(titleSubnode);
        when(titleSubnode.getNodeValue()).thenReturn(PROVIDER_NAME);
    }
}
