package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@AllArgsConstructor
public class CompositionMapper {

    private final AuthorMapper authorMapper;

    private static final String SNOMED = "371531000";

    public Composition mapComposition(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {

        EpisodeOfCare episodeOfCare = (EpisodeOfCare) encounter.getEpisodeOfCareFirstRep().getResource();

        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.USUAL);
        identifier.setValue(clinicalDocument.getRelatedDocumentArray(0).getParentDocument().getIdArray(0).getRoot());

        Composition composition = new Composition();
        composition.setIdElement(newRandomUuid());
        composition
            .setTitle("111 Report")
            .setType(new CodeableConcept()
                    .setText(SNOMED))
            .setStatus(Composition.CompositionStatus.FINAL)
            .setConfidentiality(Composition.DocumentConfidentiality.valueOf(clinicalDocument.getConfidentialityCode().getCode()))
            .setEncounter(new Reference(encounter))
            .setEncounterTarget(encounter)
            .setSubject(encounter.getSubject())
            .setSubjectTarget(encounter.getSubjectTarget())
            .setCustodian(episodeOfCare.getManagingOrganization())
            .setCustodianTarget(episodeOfCare.getManagingOrganizationTarget());

        composition.addRelatesTo()
            .setCode(Composition.DocumentRelationshipType.REPLACES);
        composition.addRelatesTo()
            .setTarget(identifier);

        for (POCDMT000002UK01Author author: clinicalDocument.getAuthorArray()){
            Encounter.EncounterParticipantComponent authorComponent = (authorMapper.mapAuthorIntoParticipantComponent(author));
            composition.addAuthor(authorComponent.getIndividual());
        }

        for (POCDMT000002UK01Component3 component3: clinicalDocument.getComponent().getStructuredBody().getComponentArray()){
            POCDMT000002UK01Section section = component3.getSection();
            Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
            sectionComponent.setTitle(section.getTitle().xmlText());
            Narrative narrative = new Narrative();
            narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
            String divText = section.getText().xmlText();
            XhtmlNode xhtmlNode = new XhtmlNode();
            xhtmlNode.addText(divText);
            narrative.setDiv(xhtmlNode);
            sectionComponent.setText(narrative);
            composition.addSection(sectionComponent);
        }

        return composition;
    }
}
