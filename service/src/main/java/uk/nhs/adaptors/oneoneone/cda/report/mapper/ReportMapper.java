package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.List;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
public class ReportMapper {
    public Encounter mapReport(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        /*
        class:      Encounter
        usage:      carrying information arising from an interaction between a patient and healthcare provider(s)
        purpose:    providing healthcare service(s) or assessing the health status of a patient.
         */
        Encounter encounter = new Encounter();

        /*
        field:      id : id
        purpose:    logical id of this artifact, that is of this encounter
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setId()

        /*
        field:      meta : Meta
        purpose:    metadata about the resource
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setMeta()

        /*
        field:      implicitRules : uri
        purpose:    a set of rules under which this content was created
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setImplicitRules()

        /*
        field:      language : code
        purpose:    language of the resource content
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setLanguage()

        /*
        field:      text : Narrative
        purpose:    text summary of the resource, for human interpretation
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setText()

        /*
        field:      contained : Resource
        purpose:    contained, inline resources
        number:     0..*
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setContained()

        /*
        field:      extension (encounterTransport) : Extension
        purpose:    uk-specific extension, used to include Transport used by the subject of an Encounter
        number:     0..1
         */
        //TODO: 2020-05-29: should the extension be on list of modifierExtension?

        /*
        field:      extension (outcomeOfAttendance) : Extension
        purpose:    uk-specific extension, used to record the outcome of an Out-Patient attendance
        number:     0..1
         */
        //TODO: 2020-05-29: should the extension be on list of modifierExtension?

        /*
        field:      extension (emergencyCareDischargeStatus) : Extension
        purpose:    uk-specific extension, used to indicate the status of the Patient on discharge from Emergency Care Department
        number:     0..1
         */
        //TODO: 2020-05-29: should the extension be on list of modifierExtension?

        /*
        field:      modifierExtension : Extension
        purpose:    list of extensions that cannot be ignored
        number:     0..*
         */
        //TODO: 2020-05-29: encounter.setExtension(List<Extension)

        /*
        field:      identifier : Identifier
        purpose:    business identifier of the Encounter, assigned by Emergency Care Department
        number:     0..*
         */
        //TODO: 2020-05-29: encounter.setIdentifier();

        /*
        MANDATORY
        field:      status : code
        purpose:    Encounter Status: planned | arrived | triaged | in-progress | onleave | finished | cancelled
        number:     1
         */
        //TODO: 2020-05-29: NIAD-294 should it be always FINISHED? or should we also use other statuses
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);

        /*
        field:      statusHistory : BackboneElement
        purpose:    list of past encounter statuses
        number:     0..*
        usage:      To be populated when the the status changes.
         */
        //TODO: 2020-05-29: encounter.setStatusHistory(List<Encounter.StatusHistoryComponent>), Encounter.StatusHistoryComponent statusHistoryComponent = mapStatusHistory();


        /*
        field:      type : CodeableConcept
        purpose:    specific type of encounter -> EncounterType
        number:     0..*
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setType()

        /*
        field:      subject : Reference (Patient | Group)
        purpose:    the patient or group of patients present at the encounter
        number:     0..1
        usage:      This MUST be populated with a reference to the Patient resource.
         */
        //TODO: 2020-05-29: NIAD-292 encounter.setSubject(), encounter.setSubjectTarget()

        /*
        field:      episodeOfCare : Reference (EpisodeOfCare)
        purpose:    episode(s) of care that this encounter should be recorded against
        number:     0..*
        usage:      If this is a continuation of a prior episode, this Encounter MUST reference that episode.
                    If not a continuation, this MUST be populated with a new episode.
         */
        //TODO: 2020-05-29:  NIAD-282 encounter.setEpisodeOfCare(List<Reference>)

        /*
        field:      incomingReferral : Reference (ReferralRequest)
        purpose:    the ReferralRequest that initiated this encounter
        number:     0..*
        usage:      This SHOULD be populated where this is a continuation of a patient journey from a different provider.
         */
        //TODO: 2020-05-29: NIAD-279 encounter.setIncomingReferral(List<Reference>)

        /*
        field:      participant : BackboneElement
        purpose:    list of participants involved in the encounter
        number:     0..*
        usage:      SHOULD be populated with the details of the Emergency Care Department system users (Practitioner)
                    during this Encounter and any third parties answering questions on behalf of the patient (RelatedPerson)
         */
        encounter.setParticipant(getEncounterParticipantComponents(clinicalDocument));

        /*
        field:      appointment : Reference (UEC Appointment)
        purpose:    the appointment that scheduled this encounter
        number:     0..1
        usage:      This MAY be populated, but is not expected to be for unscheduled care.
         */
        //TODO: 2020-05-29: NIAD-280 encounter.setAppointment(), encounter.setAppointmentTarget()

        /*
        field:      period : Period
        purpose:    the start and end time of the encounter
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 extract this snippet to separate method
        encounter.setPeriod(getPeriod(clinicalDocument));

        /*
        field:      length : Duration
        purpose:    quantity of time the encounter lasted (less time absent)
        number:     0..1
         */
        //TODO: 2020-05-29: NIAD-294 encounter.setLength()

        /*
        field:      reason : CodeableConcept
        purpose:    reason to the encounter
        number:     0..*
        usage:      This MAY be populated, but is not expected to be for unscheduled care.
         */
        //TODO: 2020-05-29: NIAD -294 encounter.setReason()

        /*
        field:      diagnosis
        purpose:    list of diagnoses relevant to this encounter
        number:     0..*
        usage:      This MAY be populated, but is not expected to be for unscheduled care.
         */
        // TODO: 2020-05-29 NIAD-293 encounter.setDiagnosis(List<Encounter.DiagnosisComponent>), Encounter.DiagnosisComponent diagnosisComponent = mapDiagnosisComponent();

        /*
        field:      location
        purpose:    list of locations where the patient has been
        number:     0..*
        usage:      This SHOULD be populated where the patient has physically attended the provider service.
         */
        // TODO: 2020-05-29 NIAD-281 encounter.setLocation(List<Encounter.EncounterLocationComponent>) Encounter.EncounterLocationComponent encounterLocationComponent = mapEncounterLocationComponent();

        /*
        field:      serviceProvider
        purpose:    the custodian organization of this Encounter record
        number:     0..1
        usage:      This MUST be populated with a reference to the Service Provider Organization responsible for the encounter
         */
        // TODO: 2020-05-29 NIAD-283 encounter.setServiceProvider(), encounter.setServiceProviderTarget()

        return encounter;
    }

    private Period getPeriod(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        IVLTS effectiveTime = clinicalDocument.getComponentOf()
            .getEncompassingEncounter()
            .getEffectiveTime();

        return PeriodMapper.mapPeriod(effectiveTime);
    }

    private List<Encounter.EncounterParticipantComponent> getEncounterParticipantComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return ParticipantMapper
                .mapEncounterParticipants(clinicalDocument
                    .getComponentOf()
                    .getEncompassingEncounter()
                    .getEncounterParticipantArray());
    }
}
