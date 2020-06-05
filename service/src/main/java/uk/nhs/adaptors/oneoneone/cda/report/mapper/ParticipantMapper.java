package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {

    private PeriodMapper periodMapper;

    private PractitionerMapper practitionerMapper;

    public ParticipantMapper(PeriodMapper periodMapper, PractitionerMapper practitionerMapper) {
        this.periodMapper = periodMapper;
        this.practitionerMapper = practitionerMapper;
    }

    public Encounter.EncounterParticipantComponent mapEncounterParticipant(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        Practitioner practitioner = practitionerMapper
            .mapPractitioner(encounterParticipant.getAssignedEntity());

        return new Encounter.EncounterParticipantComponent()
            .setType(retrieveEncounterTypeFromITK(encounterParticipant))
            .setPeriod(periodMapper.mapPeriod(encounterParticipant.getTime()))
            .setIndividual(new Reference(practitioner))
            .setIndividualTarget(practitioner);
    }

    private List<CodeableConcept> retrieveEncounterTypeFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return Collections.singletonList(new CodeableConcept()
            .setText(encounterParticipant.getTypeCode().toString()));
    }
}
