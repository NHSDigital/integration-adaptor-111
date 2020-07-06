package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.CS;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Authorization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Consent;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.StrucDocText;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsentMapperTest {
    private static final String URN_UUID = "urn:uuid:";
    private static final String LANG = "EN";
    private static final String ITK_SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private static final String INFORMATION_ADVICE_GIVEN = "887031000000108";
    private static final String OPT_OUT_URI = "http://hl7.org/fhir/ConsentPolicy/opt-out";
    private static final String ROOT_ID = "411910CF-1A76-4330-98FE-C345DDEE5553";
    private static final String CODE_DISPLAY_NAME = "Code DisplayName";

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @Mock
    private Period period;

    @Mock
    private POCDMT000002UK01Section section;

    @Mock
    private POCDMT000002UK01Component5 component5;

    @Mock
    private ST title;

    @Mock
    private CE code;

    @Mock
    private CS cs;

    @Mock
    private II ii;

    @Mock
    private StrucDocText strucDocText;

    @Mock
    private POCDMT000002UK01Consent authConsent;

    @Mock
    private POCDMT000002UK01Entry entry;

    @InjectMocks
    private ConsentMapper consentMapper;

    @Before
    public void setUp() {
        POCDMT000002UK01Component2 component2 = mock(POCDMT000002UK01Component2.class);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        POCDMT000002UK01Authorization authorization = mock(POCDMT000002UK01Authorization.class);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);

        code = CE.Factory.newInstance();
        code.setCodeSystem(ITK_SNOMED);
        code.setCode(INFORMATION_ADVICE_GIVEN);
        code.setDisplayName(CODE_DISPLAY_NAME);

        title.setLanguage(LANG);
        cs.setCode(LANG);

        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(clinicalDocument.sizeOfAuthorizationArray()).thenReturn(1);
        when(clinicalDocument.getAuthorizationArray()).thenReturn(new POCDMT000002UK01Authorization[]{authorization});

        when(authorization.getConsent()).thenReturn(authConsent);
        when(authConsent.isSetCode()).thenReturn(true);
        when(authConsent.getCode()).thenReturn(code);

        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(component3.getSection()).thenReturn(section);
        when(component5.getSection()).thenReturn(section);
        when(structuredBody.getComponentArray()).thenReturn(new POCDMT000002UK01Component3[]{component3});
    }

    @Test
    public void shouldMapITKReportToConsent() {
        Patient patient = mock(Patient.class);
        Reference patientReference = new Reference(patient);

        mockEncounter(patient, patientReference);
        mockSection();

        Consent consent = consentMapper.mapConsent(clinicalDocument, encounter);

        assertThat(consent).isNotNull();
        assertThat(consent.getIdElement().getValue()).startsWith(URN_UUID);
        assertThat(consent.getStatus()).isEqualTo(Consent.ConsentState.ACTIVE);

        assertThat(consent.hasDateTime()).isTrue();
        assertThat(consent.getLanguage()).isEqualTo(LANG);
        assertThat(consent.getPeriod()).isEqualTo(period);
        assertThat(consent.getText()).isNotNull();
        assertThat(consent.getPolicyRule()).isEqualTo(OPT_OUT_URI);
        assertThat(consent.getActionFirstRep().getCodingFirstRep().getCode()).isEqualTo(code.getCode());
        assertThat(consent.getPatient()).isEqualTo(patientReference);
        assertThat(consent.getOrganization().get(0)).isEqualTo(patientReference);
        assertThat(consent.getConsentingParty().get(0)).isEqualTo(patientReference);
    }

    private void mockEncounter(Patient patient, Reference patientReference) {
        when(encounter.getSubject()).thenReturn(patientReference);
        when(encounter.getSubjectTarget()).thenReturn(patient);
        when(encounter.getServiceProvider()).thenReturn(patientReference);
        when(encounter.getPeriod()).thenReturn(period);
        when(encounter.getLanguage()).thenReturn(LANG);
    }

    private void mockSection() {
        when(section.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[]{component5, component5});
        when(section.getEntryArray()).thenReturn(new POCDMT000002UK01Entry[]{entry});
        when(section.getCode()).thenReturn(code);
    }
}