package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;

import java.util.Date;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    private final HealthcareServiceMapper healthcareServiceMapper;
    private final PatientMapper patientMapper;

    private Reference transformerDevice = new Reference("Device/1");

    public ReferralRequest mapReferralRequest(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {

        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setIdElement(newRandomUuid());

        Patient fhirPatient = (Patient) encounter.getSubjectTarget();
        Reference patientRef = new Reference(fhirPatient);

        Date now = new Date();
        referralRequest
                .setStatus(ReferralRequest.ReferralRequestStatus.ACTIVE)
                .setIntent(ReferralRequest.ReferralCategory.PLAN)
                .setPriority(ReferralRequest.ReferralPriority.ROUTINE)
                .setSubjectTarget(fhirPatient)
                .setSubject(patientRef)
                .setContextTarget(encounter)
                .setContext(new Reference(encounter))
                .setOccurrence(new Period()
                        .setStart(now)
                        .setEnd(Date.from(now.toInstant().plusSeconds(60 * 60))))
                .setAuthoredOn(now)
                .setRequester(new ReferralRequest.ReferralRequestRequesterComponent()
                        .setAgent(transformerDevice)
                        .setOnBehalfOf(encounter.getServiceProvider()));

        for (POCDMT000002UK01InformationRecipient recipient :
                clinicalDocument.getInformationRecipientArray()) {
            referralRequest.addRecipient(new Reference(healthcareServiceMapper.mapHealthcareService(recipient)));
        }

        return referralRequest;
    }

}
