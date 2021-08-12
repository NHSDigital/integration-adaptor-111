package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.assertj.core.util.Arrays;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.CS;
import uk.nhs.connect.iucds.cda.ucr.EN;
import uk.nhs.connect.iucds.cda.ucr.II;
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

    private static final String NHS_NUMBER_VERIFIED_OID = "2.16.840.1.113883.2.1.4.1";
    private static final String NHS_NUMBER = "99937478324";
    private static final String NHS_VERIFICATION_STATUS =
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-NHSNumberVerificationStatus-1";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
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

    @Mock
    private ResourceUtil resourceUtil;

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldMapPatient() {
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        POCDMT000002UK01Patient itkPatient = mock(POCDMT000002UK01Patient.class);
        when(patientRole.isSetPatient()).thenReturn(true);
        when(patientRole.getPatient()).thenReturn(itkPatient);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        mockNames(itkPatient);
        mockAddress(patientRole);
        mockIdentifier(patientRole);
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

        assertThat(fhirPatient.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
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
        verifyNhsNumber(fhirPatient);
    }

    @Test
    public void shouldMapPatientNullification() {
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        when(patientRole.isSetPatient()).thenReturn(false);
        mockIdentifier(patientRole);

        Patient patient = patientMapper.mapPatient(patientRole);
        assertThat(patient.getActive()).isEqualTo(true);
        verifyNhsNumber(patient);
    }

    private void verifyNhsNumber(Patient fhirPatient) {
        assertThat(fhirPatient.getIdentifierFirstRep().getValue()).isEqualTo(NHS_NUMBER);
        Extension extension = fhirPatient.getIdentifierFirstRep().getExtensionFirstRep();
        assertThat(extension.getUrl()).isEqualTo(NHS_VERIFICATION_STATUS);
        Type value = extension.getValue();
        assertThat(value).isInstanceOf(CodeableConcept.class);
        CodeableConcept codeableConcept = (CodeableConcept) value;
        assertThat(codeableConcept.getCodingFirstRep().getSystem()).isEqualTo(NHS_VERIFICATION_STATUS);
        assertThat(codeableConcept.getCodingFirstRep().getCode()).isEqualTo("01");
    }

    private void mockIdentifier(POCDMT000002UK01PatientRole patientRole) {
        II id = mock(II.class);
        when(id.getRoot()).thenReturn(NHS_NUMBER_VERIFIED_OID);
        when(id.getExtension()).thenReturn(NHS_NUMBER);
        II[] ids = new II[] {id};
        when(patientRole.getIdArray()).thenReturn(ids);
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
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(organization);
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