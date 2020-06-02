package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.Encounter;
import org.springframework.stereotype.Component;

@Component
public class EncounterMapper {

    public Encounter mapEncounter() {
        Encounter encounter = new Encounter();
        encounter.setIdElement(newRandomUuid());
        encounter.setStatus(FINISHED);

        return encounter;
    }
}
