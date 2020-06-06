package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InformantMapper {

    private PractitionerMapper practitionerMapper;

    public Optional<Encounter.EncounterParticipantComponent> mapInformantIntoParticipantComponent(POCDMT000002UK01Informant12 informant){
        if(informant.isSetAssignedEntity()) {
            Encounter.EncounterParticipantComponent component = new Encounter.EncounterParticipantComponent();
            component.setType(retrieveTypeFromITK(informant));
            Practitioner practitioner = practitionerMapper
                .mapPractitioner(informant.getAssignedEntity());
            component.setIndividualTarget(practitioner);
            component.setIndividual(new Reference(practitioner));
            return Optional.of(component);
        } else {
            return Optional.empty();
        }
    }

    private List<CodeableConcept> retrieveTypeFromITK(POCDMT000002UK01Informant12 informant) {
        return Collections.singletonList(new CodeableConcept()
            .setText(informant.getTypeCode()));
    }
}
