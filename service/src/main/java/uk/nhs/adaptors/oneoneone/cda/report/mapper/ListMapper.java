package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.Date;
import java.util.List;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@RequiredArgsConstructor
public class ListMapper {

    private static final String SNOMED = "225390008";
    private static final String LIST_TITLE = "111 Report List";
    private static final String ORDER_BY = "event-date";
    private static final Reference transformerDevice = new Reference("Device/1");

    public ListResource mapList(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter, List<DomainResource> resourcesCreated){
        ListResource listResource = new ListResource();

        listResource.setIdElement(newRandomUuid());

        Identifier docIdentifier = new Identifier();
        docIdentifier.setUse(Identifier.IdentifierUse.USUAL);
        docIdentifier.setValue(clinicalDocument.getSetId().getRoot());

        listResource
                .setStatus(ListResource.ListStatus.CURRENT)
                .setTitle(LIST_TITLE)
                .setMode(ListResource.ListMode.WORKING)
                .setCode(new CodeableConcept().setText(SNOMED))
                .setSubject(encounter.getSubject())
                .setSourceTarget(encounter.getSubjectTarget())
                .setEncounter(new Reference(encounter))
                .setEncounterTarget(encounter)
        .setDate(new Date())
        .setSource(transformerDevice)
        .setOrderedBy(new CodeableConcept().setText(ORDER_BY));

        resourcesCreated.stream()
                .map(Resource::getIdElement)
                .map(Reference::new)
                .map(ListResource.ListEntryComponent::new)
                .forEach(listResource::addEntry);

        return  listResource;
    }
}
