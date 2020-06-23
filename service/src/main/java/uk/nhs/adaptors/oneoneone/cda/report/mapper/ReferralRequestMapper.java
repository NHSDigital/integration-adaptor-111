package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.*;

import java.util.Date;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    private final HealthcareServiceMapper healthcareServiceMapper;
    private final PatientMapper patientMapper;

    private Reference transformerDevice = new Reference("Device/1");

    public ReferralRequest mapPatient(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {

        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setIdElement(newRandomUuid());
        POCDMT000002UK01PatientRole patient = clinicalDocument.getRecordTargetArray(0).getPatientRole();

        Patient fhirPatient = patientMapper.mapPatient(patient);
        Reference patientRef = new Reference(fhirPatient);

        Date now = new Date();
        referralRequest
                .setStatus(ReferralRequest.ReferralRequestStatus.ACTIVE)
                .setIntent(ReferralRequest.ReferralCategory.PLAN)
                .setPriority(ReferralRequest.ReferralPriority.ROUTINE)
                .setSubject(patientRef)
                .setContext(new Reference(encounter))
                .setOccurrence(new Period()
                        .setStart(now)
                        .setEnd(Date.from(now.toInstant().plusSeconds(60 * 60))))
                .setAuthoredOn(now)
                .setRequester(new ReferralRequest.ReferralRequestRequesterComponent()
                        .setAgent(transformerDevice)
                        .setOnBehalfOf(new Reference(encounter.getServiceProvider().toString())));

        for (POCDMT000002UK01InformationRecipient recipient :
                clinicalDocument.getInformationRecipientArray()) {
            referralRequest.addRecipient(new Reference(healthcareServiceMapper.transformRecipient(recipient)));
        }

        return referralRequest;
    }

}
