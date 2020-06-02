package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.List;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

public class EncounterParticipantMapper {

    public static List<Encounter.EncounterParticipantComponent> mapEncounterParticipants(POCDMT000002UK01EncounterParticipant[] encounterParticipantArray) {

        List<Encounter.EncounterParticipantComponent> encounterParticipantComponentsList = new ArrayList<>();

        for(POCDMT000002UK01EncounterParticipant encounterParticipant : encounterParticipantArray) {
            Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent();
            encounterParticipantComponent.setPeriod(PeriodMapper.mapPeriod(encounterParticipant.getTime()));
            encounterParticipantComponent.setType(retrieveEncounterTypeFromITK(encounterParticipant));
            encounterParticipantComponent.setIndividual(retrieveIndividualFromITK(encounterParticipant));
            encounterParticipantComponent.setIndividualTarget(retrieveIndividualTargetFromITK(encounterParticipant));

            encounterParticipantComponentsList.add(encounterParticipantComponent);
        }
        return encounterParticipantComponentsList;
    }

    private static List<CodeableConcept> retrieveEncounterTypeFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        List<CodeableConcept> list = new ArrayList<>();
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(encounterParticipant.getTypeCode().toString());
        list.add(codeableConcept);
        return list;
    }

    private static Reference retrieveIndividualFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        return ReferenceMapper.mapReference();
    }

    private static Resource retrieveIndividualTargetFromITK(POCDMT000002UK01EncounterParticipant encounterParticipant) {
        POCDMT000002UK01AssignedEntity assignedEntity = encounterParticipant.getAssignedEntity();
        return PractitionerMapper.mapPractitioner(assignedEntity);
    }
}
