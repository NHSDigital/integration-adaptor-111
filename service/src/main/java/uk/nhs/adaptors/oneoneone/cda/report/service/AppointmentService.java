package uk.nhs.adaptors.oneoneone.cda.report.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.AppointmentMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@Component
@AllArgsConstructor
public class AppointmentService {

    private static final String APPOINTMENT_REFERENCE = "COCD_TP146093GB01#AppointmentReference";
    private static final String NPFIT_CDA_CONTENT = "2.16.840.1.113883.2.1.3.2.4.18.16";
    private static final String APPOINTMENT_CODE = "749001000000101";
    private static final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private final AppointmentMapper appointmentMapper;

    public Optional<Appointment> retrieveAppointment(Reference patient, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        POCDMT000002UK01StructuredBody structuredBody = clinicalDocument.getComponent().getStructuredBody();
        POCDMT000002UK01Component3[] components = structuredBody.getComponentArray();
        if (components == null) {
            return Optional.empty();
        }

        Optional<POCDMT000002UK01Entry> entry = getAppointmentEntry(components);
        Optional<POCDMT000002UK01Section> matchingSection = getAppointmentSection(components);
        if (entry.isPresent() && matchingSection.isPresent()) {
            return appointmentMapper.mapAppointment(entry.get(), matchingSection.get(), patient);
        } else {
            return Optional.empty();
        }
    }

    private Optional<POCDMT000002UK01Entry> getAppointmentEntry(POCDMT000002UK01Component3[] components) {
        List<POCDMT000002UK01Entry> appointmentEntries = Arrays.stream(components)
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .flatMap(section -> Arrays.stream(section.getEntryArray()))
            .filter(entry -> entry.isSetContentId()
                && entry.getContentId().getRoot().equals(NPFIT_CDA_CONTENT)
                && entry.getContentId().getExtension().equals(APPOINTMENT_REFERENCE))
            .collect(Collectors.toList());

        if (appointmentEntries.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(appointmentEntries.get(0));
        }
    }

    private Optional<POCDMT000002UK01Section> getAppointmentSection(POCDMT000002UK01Component3[] components) {
        List<POCDMT000002UK01Section> appointmentSections = Arrays.stream(components)
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .flatMap(section -> Arrays.stream(section.getComponentArray()))
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .filter(section -> section.getCode() != null
                && section.getCode().getCode().equals(APPOINTMENT_CODE)
                && section.getCode().getCodeSystem().equals(SNOMED))
            .collect(Collectors.toList());

        if (appointmentSections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(appointmentSections.get(0));
        }
    }
}
