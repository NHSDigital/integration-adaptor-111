package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.properties.QueueProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Service
@AllArgsConstructor
public class EncounterReportService {

    private EncounterReportBundleService encounterReportBundleService;

    private RabbitTemplate rabbitTemplate;

    private FhirContext fhirContext;

    private QueueProperties queueProperties;

    public void transformAndPopulateToGP(POCDMT000002UK01ClinicalDocument1 clinicalDocumentDocument) {
        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(clinicalDocumentDocument);

        rabbitTemplate.convertAndSend(queueProperties.getExchange(), queueProperties.getRoutingKey(),
            toJsonString(encounterBundle));
    }

    private String toJsonString(Bundle encounterBundle) {
        return fhirContext
            .newJsonParser()
            .encodeResourceToString(encounterBundle);
    }
}
