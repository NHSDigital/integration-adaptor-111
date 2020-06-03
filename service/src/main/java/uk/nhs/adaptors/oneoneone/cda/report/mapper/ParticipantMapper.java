package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;

public class ParticipantMapper {

    public static List<Encounter.EncounterParticipantComponent> mapEncounterParticipants(POCDMT000002UK01EncounterParticipant[] encounterParticipantArray) {
        return Arrays.stream(encounterParticipantArray)
            .map(ParticipantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
    }

    private static Encounter.EncounterParticipantComponent mapEncounterParticipant(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return new Encounter.EncounterParticipantComponent()
            .setType(retrieveEncounterTypeFromITK(encounterParticipant))
            .setPeriod(PeriodMapper.mapPeriod(encounterParticipant.getTime()))
            .setIndividual(retrieveIndividualFromITK(encounterParticipant));
    }

    private static List<CodeableConcept> retrieveEncounterTypeFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return Collections.singletonList(new CodeableConcept()
            .setText(encounterParticipant.getTypeCode().toString()));
    }

    private static Reference retrieveIndividualFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return new Reference(PractitionerMapper
            .mapPractitioner(encounterParticipant.getAssignedEntity()));
    }
}
