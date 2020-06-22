package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.service.HealthcareServiceService;

import uk.nhs.connect.iucds.cda.ucr.*;

import java.util.Date;

import java.util.Base64;


@Component
@RequiredArgsConstructor
public class ReferralRequestMapper {

    private final HealthcareServiceService healthcareServiceService;
    private final PatientMapper patientMapper;
    private final EncounterMapper encounterMapper;

    public ReferralRequest transform(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {

        //TODO: Pathways - to discuss with team

//        String s = clinicalDocument.getComponent().getStructuredBody().getComponentArray(0)
//                .getSection().getEntryArray(0).getObservationMedia().getValue().xmlText();
//
//        int startPosition = s.indexOf(">\n                ") + ">\n                ".length();
//        int endPosition = s.indexOf("<", startPosition);
//        String subS = s.substring(startPosition, endPosition);
//
//        byte[] decoded = Base64.getDecoder().decode(subS.getBytes());

        ReferralRequest referralRequest = new ReferralRequest();
        POCDMT000002UK01PatientRole patient = clinicalDocument.getRecordTargetArray(0).getPatientRole();

        Patient fhirPatient = patientMapper.transform(patient);
        Reference patientRef = new Reference(fhirPatient);

        String cdss = clinicalDocument.getComponent().getStructuredBody().getComponentArray(0)
                .getSection().getEntryArray(0).getObservationMedia().getParticipantArray(0)
                .getParticipantRole().getPlayingDevice().getManufacturerModelName().getDisplayName();
        Reference referenceCDSS = new Reference(cdss);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);

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
                        .setAgent(referenceCDSS)
                        .setOnBehalfOf(encounter.getServiceProvider()))
                .setDescription(cdss);

        for (POCDMT000002UK01InformationRecipient recipient :
                clinicalDocument.getInformationRecipientArray()) {
            referralRequest.addRecipient(healthcareServiceService.createHealthcareService(recipient));
        }

        return referralRequest;
    }

}
