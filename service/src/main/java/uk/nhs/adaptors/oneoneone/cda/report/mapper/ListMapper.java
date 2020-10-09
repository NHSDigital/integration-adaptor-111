package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.hl7.fhir.dstu3.model.Identifier.IdentifierUse.USUAL;
import static org.hl7.fhir.dstu3.model.ListResource.ListMode.WORKING;
import static org.hl7.fhir.dstu3.model.ListResource.ListStatus.CURRENT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.comparator.ResourceComparator;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@RequiredArgsConstructor
public class ListMapper {

    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String SNOMED_CODE_TRIAGE = "225390008";
    private static final String SNOMED_DISPLAY_TRIAGE = "Triage";
    private static final String LIST_TITLE = "111 Report List";
    private static final String ORDER_BY_CODE = "event-date";
    private static final String ORDER_BY_SYSTEM = "http://hl7.org/fhir/list-order";
    private static final String ORDER_BY_DISPLAY = "Sorted by Event Date";
    private static final Reference TRANSFORMER_DEVICE = new Reference("Device/1");
    private static final List<ResourceType> TRIAGE_RESOURCES = List.of(
        ResourceType.Condition, ResourceType.Questionnaire, ResourceType.QuestionnaireResponse,
        ResourceType.Observation, ResourceType.Organization, ResourceType.Practitioner, ResourceType.Provenance,
        ResourceType.ReferralRequest, ResourceType.RelatedPerson);

    private final ResourceComparator resourceComparator;

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
            .setCode(createCodeConcept())
            .setSubject(encounter.getSubject())
            .setSourceTarget(encounter.getSubjectTarget())
            .setEncounter(new Reference(encounter))
            .setEncounterTarget(encounter)
            .setDate(new Date())
            .setSource(TRANSFORMER_DEVICE)
            .setOrderedBy(createOrderByConcept());

        List<Resource> resourcesCreatedOrderedList = new ArrayList();

        resourcesCreated.stream().sequential().collect(Collectors.toCollection(() -> resourcesCreatedOrderedList));
        resourcesCreatedOrderedList.sort(resourceComparator);

        resourcesCreatedOrderedList.stream()
            .filter(it -> TRIAGE_RESOURCES.contains(it.getResourceType()))
            .map(Resource::getIdElement)
            .map(Reference::new)
            .map(ListResource.ListEntryComponent::new)
            .forEach(listResource::addEntry);

        return listResource;
    }

    private CodeableConcept createCodeConcept() {
        var coding = new Coding()
            .setSystem(SNOMED_SYSTEM)
            .setCode(SNOMED_CODE_TRIAGE)
            .setDisplay(SNOMED_DISPLAY_TRIAGE);
        return new CodeableConcept()
            .addCoding(coding);
    }

    private CodeableConcept createOrderByConcept() {
        var coding = new Coding()
            .setSystem(ORDER_BY_SYSTEM)
            .setCode(ORDER_BY_CODE)
            .setDisplay(ORDER_BY_DISPLAY);
        return new CodeableConcept()
            .addCoding(coding);
    }
}
