package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuardianMapperTest {

    @Mock
    private HumanName humanName;

    @Mock
    private Address address;

    @Mock
    private Reference reference;

    @Mock
    private Organization managingOrganization;

    @InjectMocks
    private GuardianMapper guardianMapper;

    @Mock
    private OrganizationMapper orgMapper;

    @Mock
    private HumanNameMapper humanNameMapper;

    @Mock
    private AddressMapper addressMapper;

    @Test
    public void mapGuardianTest() {
        POCDMT000002UK01Guardian guardian = mock(POCDMT000002UK01Guardian.class);
        POCDMT000002UK01Person guardianPerson = mock(POCDMT000002UK01Person.class);
        AD itkAddress = mock(AD.class);
        PN personName = mock(PN.class);

        guardian.addNewAddr();
        guardian.addNewGuardianPerson();
        guardian.addNewGuardianOrganization();

        when(guardian.isSetGuardianOrganization()).thenReturn(true);
        when(guardian.isSetGuardianPerson()).thenReturn(true);
        when(guardian.getAddrArray()).thenReturn(new uk.nhs.connect.iucds.cda.ucr.AD[]{itkAddress});
        when(guardianPerson.getNameArray(any())).thenReturn(personName);
        when(orgMapper.mapOrganization(any())).thenReturn(managingOrganization);
        when(humanNameMapper.mapHumanName(any())).thenReturn(humanName);
        when(addressMapper.mapAddress(any())).thenReturn(address);

        Patient.ContactComponent mappedContactComponent = guardianMapper.mapGuardian(guardian);

        assertThat(mappedContactComponent.getName()).isEqualTo(humanName);
        assertThat(mappedContactComponent.getAddress()).isEqualTo(address);
        assertThat(mappedContactComponent.getOrganization()).isEqualTo(reference);
        assertThat(mappedContactComponent.getOrganizationTarget()).isEqualTo(managingOrganization);
    }

    private POCDMT000002UK01Person createPerson() {
        POCDMT000002UK01Person person = POCDMT000002UK01Person.Factory.newInstance();
        uk.nhs.connect.iucds.cda.ucr.PN pn = uk.nhs.connect.iucds.cda.ucr.PN.Factory.newInstance();
        uk.nhs.connect.iucds.cda.ucr.PN[] pnArray = {pn};
        person.setNameArray(pnArray);
        return person;
    }
}
