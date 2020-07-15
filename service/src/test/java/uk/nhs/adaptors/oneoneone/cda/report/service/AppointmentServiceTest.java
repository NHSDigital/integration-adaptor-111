package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.AppointmentMapper;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.npfit.hl7.localisation.TemplateContent;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceTest {

    private static final String APPOINTMENT_REFERENCE = "COCD_TP146093GB01#AppointmentReference";
    private static final String NPFIT_CDA_CONTENT = "2.16.840.1.113883.2.1.3.2.4.18.16";
    private static final String APPOINTMENT_CODE = "749001000000101";
    private static final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    @InjectMocks
    private AppointmentService appointmentService;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private Appointment appointment;
    private Reference patient;
    private Reference referralRequest;
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument1;
    private POCDMT000002UK01Section section;

    @Before
    public void setUp() {
        clinicalDocument1 = mock(POCDMT000002UK01ClinicalDocument1.class);
        POCDMT000002UK01Component2 component2 = mock(POCDMT000002UK01Component2.class);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        section = mock(POCDMT000002UK01Section.class);
        patient = mock(Reference.class);
        referralRequest = mock(Reference.class);

        when(clinicalDocument1.getComponent()).thenReturn(component2);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(structuredBody.getComponentArray()).thenReturn(new POCDMT000002UK01Component3[] {component3});
        when(component3.getSection()).thenReturn(section);
    }

    @Test
    public void shouldCreateAppointmentForExistingAppointmentEntryAndSection() {
        mockAppointmentEntry(section);
        mockAppointmentSection(section);
        when(appointmentMapper.mapAppointment(any(), any(), any(), any())).thenReturn(Optional.of(appointment));

        Optional<Appointment> resultAppointment = appointmentService.retrieveAppointment(referralRequest, patient, clinicalDocument1);

        assertTrue(resultAppointment.isPresent());
        assertThat(resultAppointment.get()).isEqualTo(appointment);
    }

    private void mockAppointmentEntry(POCDMT000002UK01Section section) {
        TemplateContent templateContent = mock(TemplateContent.class);
        POCDMT000002UK01Entry sectionEntry = mock(POCDMT000002UK01Entry.class);

        when(section.getEntryArray()).thenReturn(new POCDMT000002UK01Entry[] {sectionEntry});
        when(sectionEntry.isSetContentId()).thenReturn(true);
        when(sectionEntry.getContentId()).thenReturn(templateContent);
        when(templateContent.getRoot()).thenReturn(NPFIT_CDA_CONTENT);
        when(templateContent.getExtension()).thenReturn(APPOINTMENT_REFERENCE);
    }

    private void mockAppointmentSection(POCDMT000002UK01Section section) {
        POCDMT000002UK01Component5 component5 = mock(POCDMT000002UK01Component5.class);
        POCDMT000002UK01Section section1 = mock(POCDMT000002UK01Section.class);
        CE code = mock(CE.class);

        when(section.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[] {component5});
        when(component5.getSection()).thenReturn(section1);
        when(section1.getCode()).thenReturn(code);
        when(code.getCode()).thenReturn(APPOINTMENT_CODE);
        when(code.getCodeSystem()).thenReturn(SNOMED);
    }

    @Test
    public void shouldReturnEmptyOptionalForNonExistingEntry() {
        when(section.getEntryArray()).thenReturn(new POCDMT000002UK01Entry[] {});
        mockAppointmentSection(section);

        Optional<Appointment> resultAppointment = appointmentService.retrieveAppointment(referralRequest, patient, clinicalDocument1);

        assertFalse(resultAppointment.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptionalForNonExistingSection() {
        when(section.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[] {});
        mockAppointmentEntry(section);

        Optional<Appointment> resultAppointment = appointmentService.retrieveAppointment(referralRequest, patient, clinicalDocument1);

        assertFalse(resultAppointment.isPresent());
    }
}
