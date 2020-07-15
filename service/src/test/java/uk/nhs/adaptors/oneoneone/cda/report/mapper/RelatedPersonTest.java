package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@RunWith(MockitoJUnitRunner.class)
public class RelatedPersonTest {

    @InjectMocks
    private RelatedPersonMapper relatedPersonMapper;
    @Mock
    private Encounter encounter;
    @Mock
    private HumanNameMapper humanNameMapper;
    @Mock
    private ContactPointMapper contactPointMapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private HumanName humanName;
    @Mock
    private ContactPoint contactPoint;
    @Mock
    private Address address;
    private POCDMT000002UK01RelatedEntity relatedEntity;
    private POCDMT000002UK01Informant12 informant12;

    @Before
    public void setup() {
        relatedEntity = POCDMT000002UK01RelatedEntity.Factory.newInstance();
        relatedEntity.setRelatedPerson(createPerson());
        relatedEntity.setTelecomArray(createTelecomArray());
        relatedEntity.setAddrArray(createAddrArray());
        informant12 = POCDMT000002UK01Informant12.Factory.newInstance();
        informant12.setRelatedEntity(relatedEntity);

        when(humanNameMapper.mapHumanName(ArgumentMatchers.any())).thenReturn(humanName);
        when(contactPointMapper.mapContactPoint(ArgumentMatchers.any())).thenReturn(contactPoint);
        when(addressMapper.mapAddress(ArgumentMatchers.any())).thenReturn(address);
    }

    private POCDMT000002UK01Person createPerson() {
        POCDMT000002UK01Person person = POCDMT000002UK01Person.Factory.newInstance();
        PN pn = PN.Factory.newInstance();
        PN[] pnArray = {pn};
        person.setNameArray(pnArray);
        return person;
    }

    private TEL[] createTelecomArray() {
        TEL tel = TEL.Factory.newInstance();
        return new TEL[] {tel};
    }

    private AD[] createAddrArray() {
        AD ad = AD.Factory.newInstance();
        return new AD[] {ad};
    }

    @Test
    public void shouldMapRelatedPersonFromRelatedEntity() {
        RelatedPerson relatedPerson = relatedPersonMapper.mapRelatedPerson(informant12, encounter);
        String uuidBeginning = "urn:uuid:";
        assertThat(relatedPerson.getIdElement().getValue()).startsWith(uuidBeginning);
        assertThat(relatedPerson.getActive()).isEqualTo(true);
        assertThat(relatedPerson.getNameFirstRep()).isEqualTo(humanName);
        assertThat(relatedPerson.getTelecomFirstRep()).isEqualTo(contactPoint);
        assertThat(relatedPerson.getAddressFirstRep()).isEqualTo(address);
        assertThat(relatedPerson.getGender()).isEqualTo(Enumerations.AdministrativeGender.UNKNOWN);
    }
}
