package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {

    @Autowired
    PeriodMapper periodMapper;

    @Autowired
    PractitionerMapper practitionerMapper;

    @Autowired
    ParticipantMapper participantMapper;

    public List<Encounter.EncounterParticipantComponent> mapEncounterParticipants(POCDMT000002UK01EncounterParticipant[] encounterParticipantArray) {
        return Arrays.stream(encounterParticipantArray)
            .map(participantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
    }

    private Encounter.EncounterParticipantComponent mapEncounterParticipant(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return new Encounter.EncounterParticipantComponent()
            .setType(retrieveEncounterTypeFromITK(encounterParticipant))
            .setPeriod(periodMapper.mapPeriod(encounterParticipant.getTime()))
            .setIndividual(retrieveIndividualFromITK(encounterParticipant));
    }

    private List<CodeableConcept> retrieveEncounterTypeFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return Collections.singletonList(new CodeableConcept()
            .setText(encounterParticipant.getTypeCode().toString()));
    }

    private Reference retrieveIndividualFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return new Reference(practitionerMapper
            .mapPractitioner(encounterParticipant.getAssignedEntity()));
    }
}
