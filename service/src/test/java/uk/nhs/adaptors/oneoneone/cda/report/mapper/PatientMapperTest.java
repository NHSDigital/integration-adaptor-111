package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.assertj.core.util.Arrays;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.CS;
import uk.nhs.connect.iucds.cda.ucr.EN;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Birthplace;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01LanguageCommunication;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Patient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Place;
import uk.nhs.connect.iucds.cda.ucr.TEL;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
public class PatientMapperTest {

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private ContactPointMapper contactPointMapper;

    @Mock
    private PeriodMapper periodMapper;

    @Mock
    private GuardianMapper guardianMapper;

    @Mock
    private HumanNameMapper humanNameMapper;

    @Mock
    private OrganizationMapper organizationMapper;

    @InjectMocks
    private PatientMapper patientMapper;

    @Mock
    private HumanName humanName;

    @Mock
    private Address address;

    @Mock
    private ContactPoint contactPoint;

    @Mock
    private Organization organization;

    @Mock
    private Patient.ContactComponent contactComponent;

    @Mock
    private Period period;

    @Mock
    private Date date;

    @Mock
    private NodeUtil nodeUtil;

    @Test
    @SuppressWarnings("MagicNumber")
    public void patientMapperTest() {
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        POCDMT000002UK01Patient itkPatient = mock(POCDMT000002UK01Patient.class);
        when(patientRole.isSetPatient()).thenReturn(true);
        when(patientRole.getPatient()).thenReturn(itkPatient);

        mockNames(itkPatient);
        mockAddress(patientRole);
        mockContactPoint(patientRole);
        mockGeneralPractitioner(patientRole);
        mockLanguage(itkPatient);
        mockGuardian(itkPatient);
        mockExtensions(itkPatient);
        mockBirthTime(itkPatient);
        mockBirthPlace(itkPatient);
        mockGender(itkPatient);
        mockMaritalStatus(itkPatient);

        Patient fhirPatient = patientMapper.mapPatient(patientRole);

        assertThat(fhirPatient.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(fhirPatient.getActive()).isEqualTo(true);
        assertThat(fhirPatient.getNameFirstRep()).isEqualTo(humanName);
        assertThat(fhirPatient.getAddressFirstRep()).isEqualTo(address);
        assertThat(fhirPatient.getTelecomFirstRep()).isEqualTo(contactPoint);
        assertThat(fhirPatient.getGeneralPractitionerFirstRep().getResource()).isEqualTo(organization);
        assertThat(fhirPatient.getLanguage()).isEqualTo("EN");
        assertThat(fhirPatient.getContactFirstRep()).isEqualTo(contactComponent);
        assertThat(fhirPatient.getExtension().size()).isEqualTo(3);
        assertThat(fhirPatient.getBirthDate()).isEqualTo(date);
        assertThat(fhirPatient.getGender().toCode()).isEqualTo("unknown");
        assertThat(fhirPatient.getMaritalStatus().getCoding().get(0).getDisplay()).isEqualTo("Married");
        assertThat(fhirPatient.getMaritalStatus().getCoding().get(0).getSystem()).isEqualTo("http://hl7.org/fhir/v3/MaritalStatus");
        assertThat(fhirPatient.getMaritalStatus().getCoding().get(0).getCode()).isEqualTo("M");
    }

    private void mockNames(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.sizeOfNameArray()).thenReturn(1);
        PN itkPersonName = mock(PN.class);
        when(itkPatient.getNameArray()).thenReturn(Arrays.array(itkPersonName));
        when(humanNameMapper.mapHumanName(any())).thenReturn(humanName);
    }

    private void mockAddress(POCDMT000002UK01PatientRole patientRole) {
        when(patientRole.sizeOfAddrArray()).thenReturn(1);
        AD itkAddress = mock(AD.class);
        when(patientRole.getAddrArray()).thenReturn(Arrays.array(itkAddress));
        when(addressMapper.mapAddress(any())).thenReturn(address);
    }

    private void mockContactPoint(POCDMT000002UK01PatientRole patientRole) {
        when(patientRole.sizeOfTelecomArray()).thenReturn(1);
        TEL itkTelecom = mock(TEL.class);
        when(patientRole.getTelecomArray()).thenReturn(Arrays.array(itkTelecom));
        when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);
    }

    private void mockGeneralPractitioner(POCDMT000002UK01PatientRole patientRole) {
        when(patientRole.isSetProviderOrganization()).thenReturn(true);
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        when(patientRole.getProviderOrganization()).thenReturn(itkOrganization);
        when(organizationMapper.mapOrganization(any())).thenReturn(organization);
    }

    private void mockLanguage(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.sizeOfLanguageCommunicationArray()).thenReturn(1);
        POCDMT000002UK01LanguageCommunication languageCommunication = mock(POCDMT000002UK01LanguageCommunication.class);
        when(itkPatient.getLanguageCommunicationArray()).thenReturn(Arrays.array(languageCommunication));
        CS languageCodeSymbol = mock(CS.class);
        when(languageCommunication.getLanguageCode()).thenReturn(languageCodeSymbol);
        when(languageCodeSymbol.getCode()).thenReturn("EN");
    }

    private void mockGuardian(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.sizeOfGuardianArray()).thenReturn(1);
        POCDMT000002UK01Guardian itkGuardian = mock(POCDMT000002UK01Guardian.class);
        when(itkPatient.getGuardianArray()).thenReturn(Arrays.array(itkGuardian));
        when(guardianMapper.mapGuardian(any())).thenReturn(contactComponent);
    }

    private void mockExtensions(POCDMT000002UK01Patient itkPatient) {
        mockEthnicGroup(itkPatient);
        mockReligiousAffiliation(itkPatient);
    }

    private void mockBirthTime(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetBirthTime()).thenReturn(true);
        TS itkBirthTime = mock(TS.class);
        when(itkPatient.getBirthTime()).thenReturn(itkBirthTime);
        when(periodMapper.mapPeriod(isA(TS.class))).thenReturn(period);
        when(period.getStart()).thenReturn(date);
    }

    private void mockBirthPlace(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetBirthplace()).thenReturn(true);
        POCDMT000002UK01Birthplace birthPlace = mock(POCDMT000002UK01Birthplace.class);
        POCDMT000002UK01Place place = mock(POCDMT000002UK01Place.class);
        EN placeName = mock(EN.class);

        when(itkPatient.getBirthplace()).thenReturn(birthPlace);
        when(itkPatient.getBirthplace().getPlace()).thenReturn(place);
        when(place.getName()).thenReturn(placeName);
    }

    private void mockGender(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetAdministrativeGenderCode()).thenReturn(true);
        CE administrativeGenderCodeEntity = mock(CE.class);
        when(itkPatient.getAdministrativeGenderCode()).thenReturn(administrativeGenderCodeEntity);
    }

    private void mockMaritalStatus(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetMaritalStatusCode()).thenReturn(true);
        CE maritalStatus = mock(CE.class);
        when(itkPatient.getMaritalStatusCode()).thenReturn(maritalStatus);
        when(maritalStatus.getCode()).thenReturn("m");
        when(maritalStatus.isSetCode()).thenReturn(true);
    }

    private void mockEthnicGroup(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetEthnicGroupCode()).thenReturn(true);
        CE ethnicGroupCodeEntity = mock(CE.class);
        when(itkPatient.getEthnicGroupCode()).thenReturn(ethnicGroupCodeEntity);
        when(ethnicGroupCodeEntity.getCode()).thenReturn("MULTI");
    }

    private void mockReligiousAffiliation(POCDMT000002UK01Patient itkPatient) {
        when(itkPatient.isSetReligiousAffiliationCode()).thenReturn(true);
        CE religiousAffiliationCodeEntity = mock(CE.class);
        when(itkPatient.getReligiousAffiliationCode()).thenReturn(religiousAffiliationCodeEntity);
        when(religiousAffiliationCodeEntity.getCode()).thenReturn("NONE");
    }
}