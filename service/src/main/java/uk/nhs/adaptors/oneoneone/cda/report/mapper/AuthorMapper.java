package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthorMapper {

    private PractitionerMapper practitionerMapper;

    private PeriodMapper periodMapper;

    public Encounter.EncounterParticipantComponent mapAuthorIntoParticipantComponent(POCDMT000002UK01Author author) {
        Practitioner practitioner = practitionerMapper
            .mapPractitioner(author.getAssignedAuthor());

        Encounter.EncounterParticipantComponent component = new Encounter.EncounterParticipantComponent();
        component.setType(retrieveTypeFromITK(author));
        component.setIndividualTarget(practitioner);
        component.setIndividual(new Reference(practitioner));
        return component;
    }

    private List<CodeableConcept> retrieveTypeFromITK(POCDMT000002UK01Author author) {
        return Collections.singletonList(new CodeableConcept()
            .setText(author.getTypeCode()));
    }
}
