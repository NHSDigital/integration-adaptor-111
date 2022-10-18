package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkReportHeader;
import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@ExtendWith(MockitoExtension.class)
public class EncounterReportServiceTest {

    private static final String ENCOUNTER_REPORT_MAPPING = "<encounter-report-mapping>";
    private static final String QUEUE_NAME = "Encounter-Report";
    private static final String MESSAGE_ID = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    private static final String TRACKING_ID = "7D6F23E0-AE1A-11DB-9808-B18E1E0994CD";
    private static final String SPECIFICATION_KEY = "urn:nhs-itk:ns:201005:interaction";
    private static final String SPECIFICATION_VALUE = "urn:nhs-itk:interaction:primaryEmergencyDepartmentRecipientNHS111CDADocument-v2-0";

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

    @Mock
    private Destination destination;

    @Test
    public void shouldTransformAndPopulateToGP() throws JMSException, XmlException {
        ItkReportHeader header = new ItkReportHeader();
        header.setTrackingId(TRACKING_ID);
        header.setSpecKey(SPECIFICATION_KEY);
        header.setSpecVal(SPECIFICATION_VALUE);
        POCDMT000002UK01ClinicalDocument1 clinicalDoc = mock(POCDMT000002UK01ClinicalDocument1.class);
        Bundle encounterBundle = mock(Bundle.class);
        when(encounterReportBundleService.createEncounterBundle(clinicalDoc, header, MESSAGE_ID)).thenReturn(encounterBundle);
        IParser parser = mock(IParser.class);
        when(fhirContext.newJsonParser()).thenReturn(parser);
        when(parser.setPrettyPrint(true)).thenReturn(parser);
        when(parser.encodeResourceToString(encounterBundle)).thenReturn(ENCOUNTER_REPORT_MAPPING);
        Session session = mock(Session.class);
        when(session.createTextMessage(any())).thenReturn(textMessage);

        encounterReportService.transformAndPopulateToGP(clinicalDoc, MESSAGE_ID, header);

        ArgumentCaptor<MessageCreator> argumentCaptor = ArgumentCaptor.forClass(MessageCreator.class);
        verify(jmsTemplate).send(any(Destination.class), argumentCaptor.capture());
        argumentCaptor.getValue().createMessage(session);
        verify(session).createTextMessage(ENCOUNTER_REPORT_MAPPING);
    }
}
