package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EncounterMapper {

    private PeriodMapper periodMapper;

    private ParticipantMapper participantMapper;

    public Encounter mapEncounter(POCDMT000002UK01EncompassingEncounter encompassingEncounter) {
        Encounter encounter = new Encounter();
        encounter.setIdElement(newRandomUuid());
        encounter.setStatus(FINISHED);
        encounter.setParticipant(getEncounterParticipantComponents(encompassingEncounter));
        encounter.setPeriod(getPeriod(encompassingEncounter));
        return encounter;
    }

    private Period getPeriod(POCDMT000002UK01EncompassingEncounter encompassingEncounter) {
        IVLTS effectiveTime = encompassingEncounter
            .getEffectiveTime();

        return periodMapper.mapPeriod(effectiveTime);
    }

    private List<Encounter.EncounterParticipantComponent> getEncounterParticipantComponents(POCDMT000002UK01EncompassingEncounter encompassingEncounter) {
        return Arrays.stream(encompassingEncounter
            .getEncounterParticipantArray())
            .map(participantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
    }
}
