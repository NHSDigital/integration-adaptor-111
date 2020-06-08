package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.TS;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EncounterMapper {

    private PeriodMapper periodMapper;

    private ParticipantMapper participantMapper;

    private AuthorMapper authorMapper;

    private InformantMapper informantMapper;

    private DataEntererMapper dataEntererMapper;

    public Encounter mapEncounter(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Encounter encounter = new Encounter();
        encounter.setIdElement(newRandomUuid());
        encounter.setStatus(FINISHED);
        encounter.setParticipant(getEncounterParticipantComponents(clinicalDocument));
        encounter.setPeriod(getPeriod(clinicalDocument));
        return encounter;
    }

    private Period getPeriod(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        TS effectiveTime = clinicalDocument.getEffectiveTime();

        return periodMapper.mapPeriod(effectiveTime);
    }

    private List<Encounter.EncounterParticipantComponent> getEncounterParticipantComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        List<Encounter.EncounterParticipantComponent> encounterParticipantComponents = Arrays.stream(clinicalDocument
            .getParticipantArray())
            .map(participantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
        if (clinicalDocument.sizeOfAuthorArray() > 0) {
            Arrays.stream(clinicalDocument.getAuthorArray())
                .map(authorMapper::mapAuthorIntoParticipantComponent)
                .forEach(encounterParticipantComponents::add);
        }
        if (clinicalDocument.sizeOfInformantArray() > 0) {
            Arrays.stream(clinicalDocument.getInformantArray())
                .map(informantMapper::mapInformantIntoParticipantComponent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(encounterParticipantComponents::add);
        }
        if (clinicalDocument.isSetDataEnterer()) {
            encounterParticipantComponents.add(dataEntererMapper
                .mapDataEntererIntoParticipantComponent(clinicalDocument.getDataEnterer()));
        }
        return encounterParticipantComponents;
    }
}
