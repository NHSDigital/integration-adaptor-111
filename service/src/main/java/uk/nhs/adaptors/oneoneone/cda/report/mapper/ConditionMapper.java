package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.CodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.StructuredBodyUtil;
import uk.nhs.connect.iucds.cda.ucr.CV;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@Component
@AllArgsConstructor
public class ConditionMapper {

    public static final String CLINICAL_DISCRIMINATOR = "COCD_TP146092GB01#ClinicalDiscriminator";
    public static final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";

    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public Condition mapCondition(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter,
        List<QuestionnaireResponse> questionnaireResponseList) {
        Condition condition = new Condition();

        condition.setIdElement(resourceUtil.newRandomUuid());

        condition
            .setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE)
            .setVerificationStatus(Condition.ConditionVerificationStatus.UNKNOWN)
            .setSubject(encounter.getSubject())
            .setContext(resourceUtil.createReference(encounter));

        if (questionnaireResponseList != null) {
            condition.setEvidence(evidenceOf(questionnaireResponseList));
        }
        addConditionReason(clinicalDocument, condition);

        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();

                for (POCDMT000002UK01Entry entry : section.getEntryArray()) {
                    if (entry.isSetEncounter()) {
                        POCDMT000002UK01Encounter itkEncounter = entry.getEncounter();
                        if (itkEncounter.isSetEffectiveTime()) {
                            condition.setAssertedDateElement(DateUtil.parse(itkEncounter.getEffectiveTime().getValue()));
                        }
                        if (itkEncounter.isSetText()) {
                            condition.addCategory(new CodeableConcept().setText(
                                    nodeUtil.getAllText(itkEncounter.getText().getDomNode())));
                        }
                    }
                }

                for (POCDMT000002UK01Component5 component : section.getComponentArray()) {
                    if (component.getSection() != null) {
                        if (component.getSection().isSetLanguageCode()) {
                            if (component.getSection().getLanguageCode().isSetCode()) {
                                condition.setLanguage(component.getSection().getLanguageCode().getCode());
                            }
                        }
                    }
                }
            }
        }
        return condition;
    }

    private List<Condition.ConditionEvidenceComponent> evidenceOf(List<QuestionnaireResponse> questionnaireResponseList) {
        return questionnaireResponseList.stream()
            .filter(Resource::hasId)
            .map(resourceUtil::createReference)
            .map(reference -> new Condition.ConditionEvidenceComponent().addDetail(reference))
            .collect(Collectors.toList());
    }

    private void addConditionReason(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Condition condition) {
        if (clinicalDocument.getComponent() != null) {
            if (clinicalDocument.getComponent().isSetStructuredBody()) {
                for (CodeableConcept reason : getClinicalDiscriminatorCodes(clinicalDocument.getComponent().getStructuredBody())) {
                    condition.setCode(reason);
                }
            }
        }
    }

    private List<CodeableConcept> getClinicalDiscriminatorCodes(POCDMT000002UK01StructuredBody structuredBody) {
        return Optional.ofNullable(structuredBody)
            .map(body -> StructuredBodyUtil.getEntriesOfType(body, CLINICAL_DISCRIMINATOR))
            .orElse(Collections.emptyList())
            .stream()
            .filter(POCDMT000002UK01Entry::isSetObservation)
            .map(POCDMT000002UK01Entry::getObservation)
            .map(obs -> (CV) obs.getValueArray(0))
            .filter(cv -> SNOMED.equals(cv.getCodeSystem()))
            .map(CodeUtil::createCodeableConceptList)
            .collect(Collectors.toUnmodifiableList());
    }
}
