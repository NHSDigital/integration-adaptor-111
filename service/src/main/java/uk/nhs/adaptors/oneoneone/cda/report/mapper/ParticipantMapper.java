package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.List;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

public class ParticipantMapper {

    public static List<Encounter.EncounterParticipantComponent> mapEncounterParticipants(POCDMT000002UK01EncounterParticipant[] encounterParticipantArray) {

        List<Encounter.EncounterParticipantComponent> encounterParticipantComponentsList = new ArrayList<>();

        for(POCDMT000002UK01EncounterParticipant encounterParticipant : encounterParticipantArray) {
            Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent();
            /*
            field:      type : CodeableConcept
            purpose:    role of the participant in the encounter (ParticipantType)
            number:     0..*
             */
            encounterParticipantComponent.setType(retrieveEncounterTypeFromITK(encounterParticipant));

            /*
            field:      period : Period
            purpose:    period of time during the encounter that the participant participated
             */
            encounterParticipantComponent.setPeriod(PeriodMapper.mapPeriod(encounterParticipant.getTime()));

            /*
            field:      individual : Reference (Practitioner | RelatedPerson)
            purpose:    person involved in the encounter other than the Patient
             */
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
        // TODO 2020-05-29: to figure out how to distinguish Practitioner from RelatedPerson in the assigned Entity
        // TODO 2020-05-29: Should the assigned entity be always a Practitioner? what about RelatedPerson?
        RelatedPersonMapper.mapRelatedPerson(assignedEntity);
        return PractitionerMapper.mapPractitioner(assignedEntity);
    }
}
