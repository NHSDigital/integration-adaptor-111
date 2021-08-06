package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@ExtendWith(MockitoExtension.class)
public class OrganizationMapperTest {

    public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
    public static final String GP_PRACTICE = "GP Practice";
    private static final String ODS_CODE = "SL3";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

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

    @Mock
    private NodeUtil nodeUtil;

    @Mock
    private II ii;

    @Mock
    private ResourceUtil resourceUtil;

    @Test
    public void shouldMapOrganization() {
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        AD itkAddress = mock(AD.class);
        TEL itkTelecom = mock(TEL.class);
        CE codeEntity = mock(CE.class);

        II[] iiArray = new II[] {ii};
        when(itkOrganization.getIdArray()).thenReturn(iiArray);
        when(ii.isSetExtension()).thenReturn(true);
        when(ii.getExtension()).thenReturn(ODS_CODE);
        when(itkOrganization.sizeOfIdArray()).thenReturn(1);

        when(itkOrganization.getAddrArray()).thenReturn(new AD[] {itkAddress});
        when(itkOrganization.getTelecomArray()).thenReturn(new TEL[] {itkTelecom});
        when(itkOrganization.isSetStandardIndustryClassCode()).thenReturn(true);
        when(itkOrganization.getStandardIndustryClassCode()).thenReturn(codeEntity);
        when(codeEntity.getDisplayName()).thenReturn(GP_PRACTICE);

        when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);
        when(addressMapper.mapAddress(any())).thenReturn(address);
        when(nodeUtil.getNodeValueString(itkOrganization.getNameArray(0))).thenReturn(ORGANIZATION_NAME);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        Organization organization = organizationMapper.mapOrganization(itkOrganization);

        assertThat(organization.getName()).isEqualTo(ORGANIZATION_NAME);
        assertThat(organization.getAddressFirstRep()).isEqualTo(address);
        assertThat(organization.getTelecomFirstRep()).isEqualTo(contactPoint);
        assertThat(organization.getTypeFirstRep().getText()).isEqualTo(GP_PRACTICE);
        assertThat(organization.getIdentifierFirstRep().getValue()).isEqualTo(ODS_CODE);
        assertThat(organization.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }
}
