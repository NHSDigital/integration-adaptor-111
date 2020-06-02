package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;

import org.hl7.fhir.dstu3.model.Encounter;
import org.junit.Test;

public class EncounterMapperTest {

    private EncounterMapper encounterMapper = new EncounterMapper();

    @Test
    public void mapEncounter() {
        Encounter encounter = encounterMapper.mapEncounter();

        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
    }
}
