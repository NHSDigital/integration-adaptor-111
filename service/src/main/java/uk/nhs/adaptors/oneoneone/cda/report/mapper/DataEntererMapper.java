package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01DataEnterer;

@Component
@AllArgsConstructor
public class DataEntererMapper {

    private final PeriodMapper periodMapper;

    private final PractitionerMapper practitionerMapper;

    public Encounter.EncounterParticipantComponent mapDataEntererIntoParticipantComponent(POCDMT000002UK01DataEnterer dataEnterer) {
        Practitioner practitioner = practitionerMapper
            .mapPractitioner(dataEnterer.getAssignedEntity());

        Encounter.EncounterParticipantComponent component = new Encounter.EncounterParticipantComponent();
        component.setType(retrieveTypeFromITK(dataEnterer));
        if (dataEnterer.isSetTime()) {
            component.setPeriod(periodMapper.mapPeriod(dataEnterer.getTime()));
        }
        component.setIndividualTarget(practitioner);
        component.setIndividual(new Reference(practitioner));
        return component;
    }

    private List<CodeableConcept> retrieveTypeFromITK(POCDMT000002UK01DataEnterer dataEnterer) {
        return Collections.singletonList(new CodeableConcept()
            .setText(dataEnterer.getTypeCode()));
    }
}
