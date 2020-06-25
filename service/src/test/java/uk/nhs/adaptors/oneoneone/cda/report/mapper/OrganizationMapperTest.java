package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Organization;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationMapperTest {

    public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
    public static final String GP_PRACTICE = "GP Practice";
    @Mock
    private ContactPointMapper contactPointMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private OrganizationMapper organizationMapper;

    @Mock
    private ContactPoint contactPoint;

    @Mock
    private Address address;

    @Test
    public void mapOrganization() {
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        ON itkOrganizationName = mockOrganizationName();
        AD itkAddress = mock(AD.class);
        TEL itkTelecom = mock(TEL.class);
        CE codeEntity = mock(CE.class);

        when(itkOrganization.getNameArray(anyInt())).thenReturn(itkOrganizationName);
        when(itkOrganization.getAddrArray()).thenReturn(new AD[]{itkAddress});
        when(itkOrganization.getTelecomArray()).thenReturn(new TEL[]{itkTelecom});
        when(itkOrganization.isSetStandardIndustryClassCode()).thenReturn(true);
        when(itkOrganization.getStandardIndustryClassCode()).thenReturn(codeEntity);
        when(codeEntity.getDisplayName()).thenReturn(GP_PRACTICE);

        when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);
        when(addressMapper.mapAddress(any())).thenReturn(address);

        Organization organization = organizationMapper.mapOrganization(itkOrganization);

        assertThat(organization.getName()).isEqualTo(ORGANIZATION_NAME);
        assertThat(organization.getAddressFirstRep()).isEqualTo(address);
        assertThat(organization.getTelecomFirstRep()).isEqualTo(contactPoint);
        assertThat(organization.getTypeFirstRep().getText()).isEqualTo(GP_PRACTICE);

    }

    private ON mockOrganizationName() {
        ON organizationName = mock(ON.class);
        Node edNode = mock(Node.class);
        Node edSubnode = mock(Node.class);
        when(organizationName.getDomNode()).thenReturn(edNode);
        when(edNode.getFirstChild()).thenReturn(edSubnode);
        when(edSubnode.getNodeValue()).thenReturn(ORGANIZATION_NAME);

        return organizationName;
    }
}
