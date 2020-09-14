package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.asList;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.hl7.fhir.dstu3.model.Identifier.IdentifierUse.USUAL;
import static org.hl7.fhir.dstu3.model.ListResource.ListMode.WORKING;
import static org.hl7.fhir.dstu3.model.ListResource.ListStatus.CURRENT;

import java.util.Collection;
import java.util.Date;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@RequiredArgsConstructor
public class ListMapper {

    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String SNOMED_CODE_TRIAGE = "225390008";
    private static final String SNOMED_DISPLAY_TRIAGE = "Triage";
    private static final String LIST_TITLE = "111 Report List";
    private static final String ORDER_BY = "event-date";
    private static final Reference TRANSFORMER_DEVICE = new Reference("Device/1");
    private static final Collection<Class> TRIAGE_RESOURCES = asList(Condition.class, Questionnaire.class, QuestionnaireResponse.class,
        Observation.class, Organization.class, Practitioner.class, Provenance.class, ReferralRequest.class, RelatedPerson.class);

    public ListResource mapList(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter,
        Collection<Resource> resourcesCreated) {
        ListResource listResource = new ListResource();

        listResource.setIdElement(newRandomUuid());

        Identifier docIdentifier = new Identifier();
        docIdentifier.setUse(USUAL);
        docIdentifier.setValue(clinicalDocument.getSetId().getRoot());

        listResource
            .setStatus(CURRENT)
            .setTitle(LIST_TITLE)
            .setMode(WORKING)
            .setCode(createCodeableConcept())
            .setSubject(encounter.getSubject())
            .setSourceTarget(encounter.getSubjectTarget())
            .setEncounter(new Reference(encounter))
            .setEncounterTarget(encounter)
            .setDate(new Date())
            .setSource(TRANSFORMER_DEVICE)
            .setOrderedBy(new CodeableConcept().setText(ORDER_BY));

        resourcesCreated.stream()
            .filter(it -> TRIAGE_RESOURCES.contains(it.getClass()))
            .map(Resource::getIdElement)
            .map(Reference::new)
            .map(ListResource.ListEntryComponent::new)
            .forEach(listResource::addEntry);

        return listResource;
    }

    private CodeableConcept createCodeableConcept() {
        Coding coding = new Coding(SNOMED_SYSTEM, SNOMED_CODE_TRIAGE, SNOMED_DISPLAY_TRIAGE);
        return new CodeableConcept(coding);
    }
}
