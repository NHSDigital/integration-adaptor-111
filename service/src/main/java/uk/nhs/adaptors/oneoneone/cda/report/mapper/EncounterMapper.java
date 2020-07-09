package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.codesystems.EncounterType;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.service.AppointmentService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.TS;

@Component
@AllArgsConstructor
public class EncounterMapper {

    private final PeriodMapper periodMapper;

    private final ParticipantMapper participantMapper;

    private final AuthorMapper authorMapper;

    private final InformantMapper informantMapper;

    private final DataEntererMapper dataEntererMapper;

    private final ServiceProviderMapper serviceProviderMapper;

    private final LocationMapper locationMapper;

    private final ReferralRequestMapper referralRequestMapper;

    private final AppointmentService appointmentService;

    private final EpisodeOfCareMapper episodeOfCareMapper;

    private final PatientMapper patientMapper;

    private final GroupMapper groupMapper;

    private final DiagnosisMapper diagnosisMapper;

    public Encounter mapEncounter(POCDMT000002UK01ClinicalDocument1 clinicalDocument, List<HealthcareService> healthcareServiceList) {
        Encounter encounter = new Encounter();
        encounter.setIdElement(newRandomUuid());
        encounter.setStatus(FINISHED);
        encounter.setParticipant(getEncounterParticipantComponents(clinicalDocument));
        encounter.setLocation(getLocationComponents(clinicalDocument));
        encounter.setPeriod(getPeriod(clinicalDocument));
        setServiceProvider(encounter, clinicalDocument);
        setSubject(encounter, clinicalDocument);
        setReferralRequest(encounter, healthcareServiceList);
        setAppointment(encounter, clinicalDocument);
        setEpisodeOfCare(encounter, clinicalDocument);
        setDiagnosis(encounter, clinicalDocument);
        setEncounterReasonAndType(encounter, clinicalDocument);
        return encounter;
    }

    private List<Encounter.EncounterParticipantComponent>
    getEncounterParticipantComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
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

            Arrays.stream(clinicalDocument.getInformantArray())
                .map(informantMapper::mapInformantRelatedPersonIntoParticipantComponent)
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

    private Period getPeriod(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        TS effectiveTime = clinicalDocument.getEffectiveTime();

        return periodMapper.mapPeriod(effectiveTime);
    }

    private void setServiceProvider(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        Organization serviceProviderOrganization = serviceProviderMapper.mapServiceProvider(clinicalDocument1.getCustodian());
        Reference serviceProvider = new Reference(serviceProviderOrganization);
        encounter.setServiceProvider(serviceProvider);
        encounter.setServiceProviderTarget(serviceProviderOrganization);
    }

    private void setSubject(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument1) {
        Optional<Patient> patient = getPatient(clinicalDocument1);
        if (patient.isPresent()) {
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

    private void setReferralRequest(Encounter encounter, List<HealthcareService> healthcareServiceList) {
        ReferralRequest referralRequest = referralRequestMapper.mapReferralRequest(encounter, healthcareServiceList);
        Reference referralRequestRef = new Reference(referralRequest);
        encounter.addIncomingReferral(referralRequestRef);
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

    private void setEpisodeOfCare(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Optional<EpisodeOfCare> episodeOfCare = episodeOfCareMapper.mapEpisodeOfCare(clinicalDocument, encounter.getSubject());
        episodeOfCare.ifPresent(ofCare -> encounter.addEpisodeOfCare(new Reference(ofCare)));
    }

    private void setDiagnosis(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        DiagnosisComponent diagnosis = diagnosisMapper.mapDiagnosis(clinicalDocument, encounter);
        encounter.addDiagnosis(diagnosis);
    }

    private void setEncounterReasonAndType(Encounter encounter, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Entry entry : section.getEntryArray()) {
                    if (entry.isSetEncounter()) {
                        POCDMT000002UK01Encounter encounterITK = entry.getEncounter();
                        if (encounterITK.isSetTypeId()) {
                            EncounterType encounterType = EncounterType.fromCode(encounterITK.getTypeId().getAssigningAuthorityName());
                            if (encounterType != null) {
                                encounter.addType(new CodeableConcept().setText(encounterType.toString()));
                            }
                        }
                        if (encounterITK.isSetCode()) {
                            String reason = encounterITK.getCode().getDisplayName();
                            if (reason != null) {
                                encounter.addReason(new CodeableConcept().setText(reason));
                            }
                        }
                    }
                }
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
