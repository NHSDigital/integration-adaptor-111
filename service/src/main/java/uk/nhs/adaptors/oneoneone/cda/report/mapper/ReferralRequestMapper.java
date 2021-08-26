package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory.PLAN;
import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority.ROUTINE;
import static org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus.ACTIVE;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    private static final int SECONDS_IN_HOUR = 60 * 60;
    private final Reference transformerDevice = new Reference("Device/1");
    private final ProcedureRequestMapper procedureRequestMapper;
    private final ResourceUtil resourceUtil;

    public ReferralRequest mapReferralRequest(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter,
        List<HealthcareService> healthcareServiceList, Reference condition) {

        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setIdElement(resourceUtil.newRandomUuid());

        Date now = new Date();
        referralRequest
            .setStatus(ACTIVE)
            .setIntent(PLAN)
            .setPriority(ROUTINE)
            .setSubjectTarget(encounter.getSubjectTarget())
            .setSubject(encounter.getSubject())
            .setContextTarget(encounter)
            .setContext(resourceUtil.createReference(encounter))
            .setOccurrence(new Period()
                .setStart(now)
                .setEnd(Date.from(now.toInstant().plusSeconds(SECONDS_IN_HOUR))))
            .setAuthoredOn(now)
            .setRequester(new ReferralRequest.ReferralRequestRequesterComponent()
                .setAgent(transformerDevice)
                .setOnBehalfOf(encounter.getServiceProvider()))
            .addReasonReference(condition)
            .addSupportingInfo(resourceUtil.createReference(procedureRequestMapper.mapProcedureRequest(clinicalDocument,
                encounter.getSubject(),
                referralRequest)));

        for (HealthcareService healthcareService : healthcareServiceList) {
            referralRequest.addRecipient(resourceUtil.createReference(healthcareService));
        }

        return referralRequest;
    }
}
