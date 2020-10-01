package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory.PLAN;
import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority.ROUTINE;
import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus.ACTIVE;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.CodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.StructuredBodyUtil;
import uk.nhs.connect.iucds.cda.ucr.CV;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    public static final String CLINICAL_DISCRIMINATOR = "COCD_TP146092GB01#ClinicalDiscriminator";
    public static final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private final Reference transformerDevice = new Reference("Device/1");
    private final ProcedureRequestMapper procedureRequestMapper;

    public ReferralRequest mapReferralRequest(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter,
        List<HealthcareService> healthcareServiceList, List<QuestionnaireResponse> questionnaireResponseList) {

        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setIdElement(newRandomUuid());

        Date now = new Date();
        referralRequest
            .setStatus(ACTIVE)
            .setIntent(PLAN)
            .setPriority(ROUTINE)
            .setSubjectTarget(encounter.getSubjectTarget())
            .setSubject(encounter.getSubject())
            .setContextTarget(encounter)
            .setContext(new Reference(encounter))
            .setOccurrence(new Period()
                .setStart(now)
                .setEnd(Date.from(now.toInstant().plusSeconds(SECONDS_IN_HOUR))))
            .setAuthoredOn(now)
            .setRequester(new ReferralRequest.ReferralRequestRequesterComponent()
                .setAgent(transformerDevice)
                .setOnBehalfOf(encounter.getServiceProvider()))
            .addSupportingInfo(new Reference(procedureRequestMapper.mapProcedureRequest(clinicalDocument)));

        for (HealthcareService healthcareService : healthcareServiceList) {
            referralRequest.addRecipient(new Reference(healthcareService));
        }

        addCondition(referralRequest, clinicalDocument, encounter, questionnaireResponseList);

        return referralRequest;
    }

    private void addCondition(ReferralRequest referralRequest, POCDMT000002UK01ClinicalDocument1 clinicalDocument,
        Encounter encounter, List<QuestionnaireResponse> questionnaireResponseList) {
        if (clinicalDocument.getComponent() != null) {
            if (clinicalDocument.getComponent().isSetStructuredBody()) {
                for (CodeableConcept code : getClinicalDiscriminatorCodes(clinicalDocument.getComponent().getStructuredBody())) {
                    referralRequest.addReasonReference(createReasonCondition(encounter, code, questionnaireResponseList));
                }
            }
        }
    }

    private List<CodeableConcept> getClinicalDiscriminatorCodes(POCDMT000002UK01StructuredBody structuredBody) {
        if (structuredBody == null) {
            return Collections.emptyList();
        }
        return StructuredBodyUtil
            .getEntriesOfType(structuredBody, CLINICAL_DISCRIMINATOR)
            .stream()
            .filter(POCDMT000002UK01Entry::isSetObservation)
            .map(POCDMT000002UK01Entry::getObservation)
            .map(obs -> (CV) obs.getValueArray(0))
            .filter(cv -> SNOMED.equals(cv.getCodeSystem()))
            .map(CodeUtil::createCodeableConceptList)
            .collect(Collectors.toUnmodifiableList());
    }

    private Reference createReasonCondition(Encounter encounter, CodeableConcept reason,
        List<QuestionnaireResponse> questionnaireResponseList) {
        Condition condition = new Condition()
            .setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE)
            .setVerificationStatus(Condition.ConditionVerificationStatus.CONFIRMED)
            .setCode(reason)
            .setSubject(encounter.getSubject())
            .setContext(new Reference(encounter))
            .setEvidence(evidenceOf(questionnaireResponseList));
        condition.setId(newRandomUuid());

        return new Reference(condition);
    }

    private List<ConditionEvidenceComponent> evidenceOf(List<QuestionnaireResponse> questionnaireResponseList) {
        return questionnaireResponseList.stream()
            .filter(Resource::hasId)
            .map(Resource::getId)
            .map(Reference::new)
            .map(reference -> new ConditionEvidenceComponent().addDetail(reference))
            .collect(Collectors.toList());
    }
}
