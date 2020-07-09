package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;

@Component
@AllArgsConstructor
public class ParticipantMapper {

    private final PeriodMapper periodMapper;

    private final PractitionerMapper practitionerMapper;

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
}
