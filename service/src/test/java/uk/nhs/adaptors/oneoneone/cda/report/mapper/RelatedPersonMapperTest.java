package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RelatedPersonMapperTest {
    private static final String ITK_CODE_SYSTEM = "2.16.840.1.113883.2.1.3.2.4.16.45";
    private static final String ITK_CODE = "01";
    private static final String ITK_DISPLAY_NAME = "Spouse";
    private static final String URN_UUID = "urn:uuid:";

    @Mock
    private CE code;

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private PeriodMapper periodMapper;

    @Mock
    private Period period;

    @Mock
    private HumanName humanName;

    @Mock
    private ContactPoint contactPoint;

    @Mock
    private Address address;

    @Mock
    private Patient patient;

    @Mock
    private Reference patientReference;

    @Mock
    private Date date;

    @Mock
    private Encounter encounter;

    @Mock
    private Practitioner practitioner;

    @Mock
    private POCDMT000002UK01Informant12 informant;

    @InjectMocks
    private RelatedPersonMapper relatedPersonMapper;

    @Before
    public void setUp() {
        POCDMT000002UK01RelatedEntity relatedEntity = mock(POCDMT000002UK01RelatedEntity.class);

        relatedEntity.setRelatedPerson(createPerson());
        relatedEntity.setTelecomArray(createTelecomArray());
        relatedEntity.setAddrArray(createAddrArray());

        patientReference = new Reference(patient);

        when(encounter.getSubject()).thenReturn(patientReference);
        when(informant.isSetRelatedEntity()).thenReturn(true);
        when(informant.getRelatedEntity()).thenReturn(relatedEntity);
        when(relatedEntity.isSetRelatedPerson()).thenReturn(true);
        when(relatedEntity.isSetEffectiveTime()).thenReturn(true);

        code = CE.Factory.newInstance();
        code.setCodeSystem(ITK_CODE_SYSTEM);
        code.setCode(ITK_CODE);
        code.setDisplayName(ITK_DISPLAY_NAME);

        when(periodMapper.mapPeriod(any())).thenReturn(period);
        when(practitionerMapper.mapPractitioner(relatedEntity)).thenReturn(practitioner);
    }

    @Test
    public void shouldMapITKReportToRelatedPerson() {
        mockPractioner();

        Optional<RelatedPerson> relatedPersonOptional = relatedPersonMapper.mapRelatedPerson(informant, encounter);
        assertThat(relatedPersonOptional).isNotEmpty();

        RelatedPerson relatedPerson = relatedPersonOptional.get();
        assertThat(relatedPerson.getIdElement().getValue()).startsWith(URN_UUID);
        assertThat(relatedPerson.getPatient()).isEqualTo(patientReference);
        assertThat(relatedPerson.getPeriod()).isEqualTo(period);
        assertThat(relatedPerson.getActive()).isEqualTo(true);
        assertThat(relatedPerson.getNameFirstRep()).isEqualTo(humanName);
        assertThat(relatedPerson.getAddressFirstRep()).isEqualTo(address);
        assertThat(relatedPerson.getTelecomFirstRep()).isEqualTo(contactPoint);
        assertThat(relatedPerson.getBirthDate()).isEqualTo(date);
    }

    private void mockPractioner() {
        when(practitioner.getBirthDate()).thenReturn(date);
        when(practitioner.getAddress()).thenReturn(List.of(address));
        when(practitioner.getName()).thenReturn(List.of(humanName));
        when(practitioner.getTelecom()).thenReturn(List.of(contactPoint));
        when(practitioner.getActive()).thenReturn(true);
    }

    private AD[] createAddrArray() {
        return new AD[]{AD.Factory.newInstance()};
    }

    private TEL[] createTelecomArray() {
        return new TEL[]{TEL.Factory.newInstance()};
    }

    private POCDMT000002UK01Person createPerson() {
        POCDMT000002UK01Person person = POCDMT000002UK01Person.Factory.newInstance();
        PN[] pnArray = {PN.Factory.newInstance()};
        person.setNameArray(pnArray);
        return person;
    }
}