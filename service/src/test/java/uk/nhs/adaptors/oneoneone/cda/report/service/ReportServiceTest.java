package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Encounter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.ReportMapper;
import uk.nhs.adaptors.oneoneone.properties.QueueProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    private static final String ENCOUNTER_REPORT_MAPPING = "<encounter-report-mapping>";
    private static final String ROUTING_KEY = "ROUTING-KEY";
    private static final String EXCHANGE = "EXCHANGE";

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private QueueProperties queueProperties;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        when(queueProperties.getRoutingKey()).thenReturn(ROUTING_KEY);
        when(queueProperties.getExchange()).thenReturn(EXCHANGE);
    }

    @Test
    public void transformAndPopulateToGP() throws JsonProcessingException {

        POCDMT000002UK01ClinicalDocument1 clinicalDoc = mock(POCDMT000002UK01ClinicalDocument1.class);
        Encounter encounter = mock(Encounter.class);
        when(reportMapper.mapReport(clinicalDoc)).thenReturn(encounter);
        when(objectMapper.writeValueAsString(encounter)).thenReturn(ENCOUNTER_REPORT_MAPPING);
        reportService.transformAndPopulateToGP(clinicalDoc);

        verify(rabbitTemplate).convertAndSend(EXCHANGE, ROUTING_KEY, ENCOUNTER_REPORT_MAPPING);
    }
}
