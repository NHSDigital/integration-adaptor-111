package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.hl7.fhir.dstu3.model.Composition.CompositionStatus.FINAL;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.hl7.fhir.dstu3.model.Identifier.IdentifierUse.USUAL;
import static org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus.GENERATED;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Composition.SectionComponent;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

@Component
@AllArgsConstructor
public class CompositionMapper {

    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String SNOMED_CODE = "371531000";
    private static final String SNOMED_CODE_DISPLAY = "Report of clinical encounter (record artifact)";
    private static final String COMPOSITION_TITLE = "111 Report";
    private final AuthorMapper authorMapper;
    private final NodeUtil nodeUtil;

    public Composition mapComposition(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter, List<CarePlan> carePlans,
        List<QuestionnaireResponse> questionnaireResponseList, ReferralRequest referralRequest) {

        Composition composition = new Composition();
        composition.setIdElement(newRandomUuid());

        Identifier docIdentifier = new Identifier();
        docIdentifier.setUse(USUAL);
        docIdentifier.setValue(clinicalDocument.getSetId().getRoot());

        composition
            .setTitle(COMPOSITION_TITLE)
            .setType(createCodeableConcept())
            .setStatus(FINAL)
            .setEncounter(new Reference(encounter))
            .setSubject(encounter.getSubject())
            .setDate(new Date())
            .setIdentifier(docIdentifier);

        if (clinicalDocument.getConfidentialityCode().isSetCode()) {
            composition
                .setConfidentiality(Composition.DocumentConfidentiality.valueOf(clinicalDocument.getConfidentialityCode().getCode()));
        }

        if (isNotEmpty(clinicalDocument.getRelatedDocumentArray())
            && clinicalDocument.getRelatedDocumentArray(0).getParentDocument().getIdArray(0).isSetRoot()) {
            Identifier relatedDocIdentifier = new Identifier();
            relatedDocIdentifier.setUse(USUAL);
            relatedDocIdentifier.setValue(clinicalDocument.getRelatedDocumentArray(0).getParentDocument().getIdArray(0).getRoot());
            composition.addRelatesTo()
                .setCode(Composition.DocumentRelationshipType.REPLACES)
                .setTarget(relatedDocIdentifier);
        }

        if (clinicalDocument.sizeOfAuthorArray() > 0) {
            for (POCDMT000002UK01Author author : clinicalDocument.getAuthorArray()) {
                Encounter.EncounterParticipantComponent authorComponent = (authorMapper.mapAuthorIntoParticipantComponent(author));
                composition.addAuthor(authorComponent.getIndividual());
            }
        }

        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                addSectionToComposition(composition, section);
            }
        }

        for (CarePlan carePlan : carePlans) {
            composition.addSection(buildSectionComponentFromResource(carePlan));
        }

        if (!referralRequest.isEmpty()) {
            composition.addSection(buildSectionComponentFromResource(referralRequest));
        }

        if (questionnaireResponseList != null) {
            addPathwaysToSection(composition, questionnaireResponseList);
        }

        return composition;
    }

    private void addSectionToComposition(Composition composition, POCDMT000002UK01Section section) {
        for (POCDMT000002UK01Component5 component5 : section.getComponentArray()) {
            POCDMT000002UK01Section innerSection = component5.getSection();
            composition.addSection(getSectionText(innerSection));

            if (isNotEmpty(innerSection.getComponentArray())) {
                addSectionToComposition(composition, innerSection);
            }
        }
    }

    private SectionComponent getSectionText(POCDMT000002UK01Section sectionComponent5) {
        SectionComponent sectionComponent = new SectionComponent();
        if (sectionComponent5.isSetTitle()) {
            sectionComponent.setTitle(nodeUtil.getAllText(sectionComponent5.getTitle().getDomNode()));
        }
        Narrative narrative = new Narrative();
        narrative.setStatus(GENERATED);
        String divText = sectionComponent5.getText().xmlText();
        XhtmlNode xhtmlNode = new XhtmlNode();
        xhtmlNode.setNodeType(NodeType.Document);
        xhtmlNode.addText(divText);
        narrative.setDiv(xhtmlNode);
        sectionComponent.setText(narrative);

        return sectionComponent;
    }

    private CodeableConcept createCodeableConcept() {
        Coding coding = new Coding(SNOMED_SYSTEM, SNOMED_CODE, SNOMED_CODE_DISPLAY);
        return new CodeableConcept(coding);
    }

    private SectionComponent buildSectionComponentFromResource(DomainResource resource) {
        return new SectionComponent()
            .setTitle(resource.fhirType())
            .addEntry(new Reference(resource));
    }

    private void addPathwaysToSection(Composition composition, List<QuestionnaireResponse> questionnaireResponseList) {
        String questionnaireResponseTitle = "QuestionnaireResponse";
        for (QuestionnaireResponse questionnaireResponse : questionnaireResponseList) {
            SectionComponent sectionComponent = new SectionComponent();
            sectionComponent.addEntry(new Reference(questionnaireResponse));
            sectionComponent.setTitle(questionnaireResponseTitle);
            composition.addSection(sectionComponent);
        }
    }
}
