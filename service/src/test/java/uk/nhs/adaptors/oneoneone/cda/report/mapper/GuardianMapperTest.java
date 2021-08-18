package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GuardianMapperTest {

    @Mock
    private HumanName humanName;

    @Mock
    private Address address;

    @Mock
    private Organization managingOrganization;

    @InjectMocks
    private GuardianMapper guardianMapper;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private HumanNameMapper humanNameMapper;

    @Mock
    private AddressMapper addressMapper;

    @Test
    public void mapGuardianTest() {
        POCDMT000002UK01Guardian guardian = mock(POCDMT000002UK01Guardian.class);

        mockGuardianPerson(guardian);
        mockAddress(guardian);
        mockGuardianOrganization(guardian);

        Patient.ContactComponent mappedContactComponent = guardianMapper.mapGuardian(guardian);

        assertThat(mappedContactComponent.getName()).isEqualTo(humanName);
        assertThat(mappedContactComponent.getAddress()).isEqualTo(address);
        assertThat(mappedContactComponent.getOrganization().getResource()).isEqualTo(managingOrganization);
        assertThat(mappedContactComponent.getOrganizationTarget()).isEqualTo(managingOrganization);
    }

    private void mockGuardianPerson(POCDMT000002UK01Guardian guardian) {
        POCDMT000002UK01Person guardianPerson = mock(POCDMT000002UK01Person.class);
        when(guardian.isSetGuardianPerson()).thenReturn(true);
        when(guardian.getGuardianPerson()).thenReturn(guardianPerson);
        PN personName = mock(PN.class);
        when(guardianPerson.sizeOfNameArray()).thenReturn(1);
        when(guardianPerson.getNameArray(0)).thenReturn(personName);
        when(humanNameMapper.mapHumanName(any())).thenReturn(humanName);
    }

    private void mockAddress(POCDMT000002UK01Guardian guardian) {
        when(guardian.sizeOfAddrArray()).thenReturn(1);
        AD itkAddress = mock(AD.class);
        when(guardian.getAddrArray(0)).thenReturn(itkAddress);
        when(addressMapper.mapAddress(any())).thenReturn(address);
    }

    private void mockGuardianOrganization(POCDMT000002UK01Guardian guardian) {
        when(guardian.isSetGuardianOrganization()).thenReturn(true);
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        when(guardian.getGuardianOrganization()).thenReturn(itkOrganization);
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(managingOrganization);
    }
}
