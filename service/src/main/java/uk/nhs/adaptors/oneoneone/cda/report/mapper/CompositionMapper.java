package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import java.util.Date;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@AllArgsConstructor
public class CompositionMapper {

    private static final String SNOMED = "371531000";
    private static final String COMPOSITION_TITLE = "111 REPORT";
    private final AuthorMapper authorMapper;

    public Composition mapComposition(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {

        Composition composition = new Composition();
        composition.setIdElement(newRandomUuid());

        if (encounter.getEpisodeOfCareFirstRep().getResource() != null){
            EpisodeOfCare episodeOfCare = (EpisodeOfCare) encounter.getEpisodeOfCareFirstRep().getResource();
            composition
                    .setCustodian(episodeOfCare.getManagingOrganization())
                    .setCustodianTarget(episodeOfCare.getManagingOrganizationTarget());
        }

        Identifier docIdentifier = new Identifier();
        docIdentifier.setUse(Identifier.IdentifierUse.USUAL);
        docIdentifier.setValue(clinicalDocument.getSetId().getRoot());

        composition
                .setTitle(COMPOSITION_TITLE)
                .setType(new CodeableConcept()
                        .setText(SNOMED))
                .setStatus(Composition.CompositionStatus.FINAL)
                .setConfidentiality(Composition.DocumentConfidentiality.valueOf(clinicalDocument.getConfidentialityCode().getCode()))
                .setEncounter(new Reference(encounter))
                .setEncounterTarget(encounter)
                .setSubject(encounter.getSubject())
                .setSubjectTarget(encounter.getSubjectTarget())
                .setDate(new Date())
                .setIdentifier(docIdentifier);

        if(clinicalDocument.getRelatedDocumentArray(0).getParentDocument().getIdArray(0).getRoot() != null){
            Identifier relatedDocIdentifier = new Identifier();
            relatedDocIdentifier.setUse(Identifier.IdentifierUse.USUAL);
            relatedDocIdentifier.setValue(clinicalDocument.getRelatedDocumentArray(0).getParentDocument().getIdArray(0).getRoot());
            composition.addRelatesTo()
                    .setCode(Composition.DocumentRelationshipType.REPLACES)
                    .setTarget(relatedDocIdentifier);
        }


        if (clinicalDocument.getAuthorArray() != null) {
            for (POCDMT000002UK01Author author : clinicalDocument.getAuthorArray()) {
                Encounter.EncounterParticipantComponent authorComponent = (authorMapper.mapAuthorIntoParticipantComponent(author));
                composition.addAuthor(authorComponent.getIndividual());
            }
        }

        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray() != null) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Component5 component5 : section.getComponentArray()) {
                    POCDMT000002UK01Section sectionComponent5 = component5.getSection();
                    Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
                    sectionComponent.setTitle(sectionComponent5.getTitle().xmlText());
                    Narrative narrative = new Narrative();
                    narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
                    String divText = sectionComponent5.getText().xmlText();
                    XhtmlNode xhtmlNode = new XhtmlNode();
                    xhtmlNode.setNodeType(NodeType.Document);
                    xhtmlNode.addText(divText);
                    narrative.setDiv(xhtmlNode);
                    sectionComponent.setText(narrative);
                    composition.addSection(sectionComponent);
                }
            }
        }

        return composition;
    }
}
