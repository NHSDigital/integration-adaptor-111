package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.ArrayList;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import org.hl7.fhir.dstu3.model.Group;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.service.AppointmentService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.TS;

import java.util.List;
import org.hl7.fhir.dstu3.model.Encounter;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.GroupMapper;

@Component
@AllArgsConstructor
public class EncounterMapper {

    private PeriodMapper periodMapper;

    private ParticipantMapper participantMapper;

    private AuthorMapper authorMapper;

    private InformantMapper informantMapper;

    private DataEntererMapper dataEntererMapper;

    private ServiceProviderMapper serviceProviderMapper;

    private LocationMapper locationMapper;

    private AppointmentService appointmentService;

    private EpisodeOfCareMapper episodeOfCareMapper;

    private PatientMapper patientMapper;

    private GroupMapper groupMapper;

    public Encounter mapEncounter(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Encounter encounter = new Encounter();
        encounter.setIdElement(newRandomUuid());
        encounter.setStatus(FINISHED);
        encounter.setParticipant(getEncounterParticipantComponents(clinicalDocument));
        encounter.setLocation(getLocationComponents(clinicalDocument));
        encounter.setPeriod(getPeriod(clinicalDocument));
        setServiceProvider(encounter, clinicalDocument);
        setAppointment(encounter, clinicalDocument);
        setSubject(encounter, clinicalDocument);
        setEpisodeOfCare(encounter, clinicalDocument);
        return encounter;
    }

    private void setEpisodeOfCare(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Optional<EpisodeOfCare> episodeOfCare = episodeOfCareMapper.mapEpisodeOfCare(clinicalDocument, encounter.getSubject());
        episodeOfCare.ifPresent(ofCare -> encounter.addEpisodeOfCare(new Reference(ofCare)));
    }

    private Period getPeriod(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        TS effectiveTime = clinicalDocument.getEffectiveTime();

        return periodMapper.mapPeriod(effectiveTime);
    }

    private List<Encounter.EncounterParticipantComponent> getEncounterParticipantComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        List<Encounter.EncounterParticipantComponent> encounterParticipantComponents = Arrays.stream(clinicalDocument
            .getParticipantArray())
            .map(participantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
        if (clinicalDocument.sizeOfAuthorArray() > 0) {
            Arrays.stream(clinicalDocument.getAuthorArray())
                .map(authorMapper::mapAuthorIntoParticipantComponent)
                .forEach(encounterParticipantComponents::add);
        }
        if (clinicalDocument.sizeOfInformantArray() > 0) {
            Arrays.stream(clinicalDocument.getInformantArray())
                .map(informantMapper::mapInformantIntoParticipantComponent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(encounterParticipantComponents::add);
        }
        if (clinicalDocument.isSetDataEnterer()) {
            encounterParticipantComponents.add(dataEntererMapper
                .mapDataEntererIntoParticipantComponent(clinicalDocument.getDataEnterer()));
        }
        return encounterParticipantComponents;
    }

    private List<Encounter.EncounterLocationComponent>
    getLocationComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        List<Encounter.EncounterLocationComponent> locations = new ArrayList<>();
        if (clinicalDocument1.sizeOfRecordTargetArray() > 0) {
            locations = Arrays.stream(clinicalDocument1.getRecordTargetArray())
                    .map(recordTarget -> recordTarget.getPatientRole().getProviderOrganization())
                    .map(locationMapper::mapOrganizationToLocationComponent)
                    .collect(Collectors.toList());
        }
        return locations;
    }

    private void setAppointment(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Reference referralRequest = encounter.getIncomingReferralFirstRep();
        Reference patient = encounter.getSubject();

        Optional<Appointment> appointment = appointmentService.retrieveAppointment(referralRequest, patient, clinicalDocument);
        if (appointment.isPresent()) {
            encounter.setAppointment(new Reference(appointment.get()));
            encounter.setAppointmentTarget(appointment.get());
        }
    }

    private void setServiceProvider(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        Organization serviceProviderOrganization = serviceProviderMapper.mapServiceProvider(clinicalDocument1.getCustodian());
        Reference serviceProvider = new Reference(serviceProviderOrganization);
        encounter.setServiceProvider(serviceProvider);
        encounter.setServiceProviderTarget(serviceProviderOrganization);
    }

    private void setSubject(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        Optional<Patient> patient = getPatient(clinicalDocument1);
        if (patient != null) {
            if (clinicalDocument1.sizeOfRecordTargetArray() == 1) {
                encounter.setSubject(new Reference(patient.get()));
                encounter.setSubjectTarget(patient.get());
            } else if (clinicalDocument1.sizeOfRecordTargetArray() > 1) {
                Group group = groupMapper.mapGroup(clinicalDocument1.getRecordTargetArray());
                encounter.setSubject(new Reference(group));
                encounter.setSubjectTarget(group);
            }
        }
    }

    private Optional<Patient> getPatient(POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        Patient patient = new Patient();
        if (clinicalDocument1.sizeOfRecordTargetArray() > 0) {
            POCDMT000002UK01PatientRole patientRole = clinicalDocument1.getRecordTargetArray(0).getPatientRole();
            patient = patientMapper.mapPatient(patientRole);
        }
        return Optional.of(patient);
    }
}
