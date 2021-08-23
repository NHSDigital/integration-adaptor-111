package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;

@Component
@AllArgsConstructor
public class ParticipantMapper {
    private static final Map<String, String> PARTICIPANT_TYPE_CODE_MAP = new HashMap<>();

    static {
        PARTICIPANT_TYPE_CODE_MAP.put("INF", "Informant");
    }

    private final PeriodMapper periodMapper;

    private final PractitionerMapper practitionerMapper;

    private final RelatedPersonMapper relatedPersonMapper;

    public Encounter.EncounterParticipantComponent mapEncounterParticipant(POCDMT000002UK01Participant1 encounterParticipant) {
        Practitioner practitioner = practitionerMapper
            .mapPractitioner(encounterParticipant.getAssociatedEntity());

        Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent()
            .setType(retrieveTypeFromITK(encounterParticipant))
            .setIndividual(new Reference(practitioner))
            .setIndividualTarget(practitioner);

        if (encounterParticipant.isSetTime()) {
            encounterParticipantComponent.setPeriod(periodMapper.mapPeriod(encounterParticipant.getTime()));
        }

        return encounterParticipantComponent;
    }

    private List<CodeableConcept> retrieveTypeFromITK(POCDMT000002UK01Participant1 encounterParticipant) {
        return Collections.singletonList(new CodeableConcept()
            .setText(encounterParticipant.getTypeCode()));
    }

    public Encounter.EncounterParticipantComponent mapEncounterRelatedPerson(POCDMT000002UK01Informant12 informant, Encounter encounter) {
        RelatedPerson relatedPerson = relatedPersonMapper
            .mapRelatedPerson(informant, encounter);

        Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent()
            .setType(Collections.singletonList(new CodeableConcept().setText(PARTICIPANT_TYPE_CODE_MAP.get(informant.getTypeCode()))))
            .setIndividual(new Reference(relatedPerson))
            .setIndividualTarget(relatedPerson);

        if (informant.isSetRelatedEntity()) {
            if (informant.getRelatedEntity().isSetEffectiveTime()) {
                encounterParticipantComponent.setPeriod(periodMapper.mapPeriod(informant.getRelatedEntity().getEffectiveTime()));
            }
        }

        return encounterParticipantComponent;
    }
}
