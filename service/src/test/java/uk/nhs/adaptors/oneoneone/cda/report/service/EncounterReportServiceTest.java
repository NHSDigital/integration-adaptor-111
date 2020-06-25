package uk.nhs.adaptors.oneoneone.cda.report.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Bundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EncounterReportServiceTest {

    private static final String ENCOUNTER_REPORT_MAPPING = "<encounter-report-mapping>";
    private static final String QUEUE_NAME = "Encounter-Report";
    private static final String MESSAGE_ID = "2B77B3F5-3016-4A6D-821F-152CE420E58D";

    @InjectMocks
    private EncounterReportService encounterReportService;

    @Mock
    private AmqpProperties amqpProperties;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private EncounterReportBundleService encounterReportBundleService;

    @Mock
    private FhirContext fhirContext;

    @Mock
    private TextMessage textMessage;

    @Before
    public void setUp() {
        when(amqpProperties.getQueueName()).thenReturn(QUEUE_NAME);
    }

    @Test
    public void transformAndPopulateToGP() throws JMSException {
        POCDMT000002UK01ClinicalDocument1 clinicalDoc = mock(POCDMT000002UK01ClinicalDocument1.class);
        Bundle encounterBundle = mock(Bundle.class);
        when(encounterReportBundleService.createEncounterBundle(clinicalDoc)).thenReturn(encounterBundle);
        IParser parser = mock(IParser.class);
        when(fhirContext.newJsonParser()).thenReturn(parser);
        when(parser.encodeResourceToString(encounterBundle)).thenReturn(ENCOUNTER_REPORT_MAPPING);
        Session session = mock(Session.class);
        when(session.createTextMessage(any())).thenReturn(textMessage);

        encounterReportService.transformAndPopulateToGP(clinicalDoc, MESSAGE_ID);

        ArgumentCaptor<MessageCreator> argumentCaptor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(jmsTemplate).send(eq(QUEUE_NAME), argumentCaptor.capture());
        argumentCaptor.getValue().createMessage(session);
        verify(session).createTextMessage(ENCOUNTER_REPORT_MAPPING);
    }
}
