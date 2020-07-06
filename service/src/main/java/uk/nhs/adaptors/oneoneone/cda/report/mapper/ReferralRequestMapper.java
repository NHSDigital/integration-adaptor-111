package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    private final Reference transformerDevice = new Reference("Device/1");

    public ReferralRequest mapReferralRequest(Encounter encounter, List<HealthcareService> healthcareServiceList) {

        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setIdElement(newRandomUuid());

        Date now = new Date();
        referralRequest
                .setStatus(ReferralRequest.ReferralRequestStatus.ACTIVE)
                .setIntent(ReferralRequest.ReferralCategory.PLAN)
                .setPriority(ReferralRequest.ReferralPriority.ROUTINE)
                .setSubjectTarget(encounter.getSubjectTarget())
                .setSubject(encounter.getSubject())
                .setContextTarget(encounter)
                .setContext(new Reference(encounter))
                .setOccurrence(new Period()
                        .setStart(now)
                        .setEnd(Date.from(now.toInstant().plusSeconds(60 * 60))))
                .setAuthoredOn(now)
                .setRequester(new ReferralRequest.ReferralRequestRequesterComponent()
                        .setAgent(transformerDevice)
                        .setOnBehalfOf(encounter.getServiceProvider()));

        for (HealthcareService healthcareService :
                healthcareServiceList) {
            referralRequest.addRecipient(new Reference(healthcareService));
        }

        return referralRequest;
    }

}
