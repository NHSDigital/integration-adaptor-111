package uk.nhs.adaptors.oneoneone.cda.report.service;

import javax.jms.TextMessage;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Service
@Slf4j
@AllArgsConstructor
public class EncounterReportService {

    private static final String MESSAGE_ID = "messageId";

    private final EncounterReportBundleService encounterReportBundleService;

    private final JmsTemplate jmsTemplate;

    private final FhirContext fhirContext;

    private final AmqpProperties amqpProperties;

    public void transformAndPopulateToGP(POCDMT000002UK01ClinicalDocument1 clinicalDocumentDocument,
        String messageId, String trackingId) throws XmlException {
        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(clinicalDocumentDocument);

        jmsTemplate.send(amqpProperties.getQueueName(), session -> {
            TextMessage message = session.createTextMessage(toJsonString(encounterBundle));
            message.setStringProperty(MESSAGE_ID, messageId);
            return message;
        });
        LOGGER.info("Successfully sent FHIR message to queue. MessageId: {}, ItkTrackingId: {}", messageId, trackingId);
    }

    private String toJsonString(Bundle encounterBundle) {
        return fhirContext
            .newJsonParser()
            .encodeResourceToString(encounterBundle);
    }
}
