package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.enums.MessageHeaderEvent.DISCHARGE_DETAILS;

import java.util.Arrays;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.MessageHeader;
import org.hl7.fhir.dstu3.model.MessageHeader.MessageSourceComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkReportHeader;
import uk.nhs.adaptors.oneoneone.cda.report.enums.MessageHeaderEvent;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@ExtendWith(MockitoExtension.class)
public class MessageHeaderServiceTest {

    private static final String MESSAGE_SOURCE_NAME = "NHS 111 Adaptor";
    private static final String ENDPOINT = "https://gp.endpoint.com";
    private static final String SPECIFICATION_KEY = "urn:nhs-itk:ns:201005:interaction";
    private static final String SPECIFICATION_VALUE = "urn:nhs-itk:interaction:copyRecipientNHS111CDADocument-v2-0";
    private static final String ADDRESS = "the_address";
    private static final String MESSAGEID = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    private static final String EFFECTIVE_TIME = "20210406123335+01";

    @Mock
    private SoapProperties soapProperties;

    @InjectMocks
    private MessageHeaderService messageHeaderService;

    @BeforeEach
    public void setUp() {
        when(soapProperties.getSendTo()).thenReturn(ENDPOINT);

    }

    @Test
    public void shouldCreateMessageHeader() {
        ItkReportHeader itkReportHeader = new ItkReportHeader();
        itkReportHeader.setSpecKey(SPECIFICATION_KEY);
        itkReportHeader.setSpecVal(SPECIFICATION_VALUE);
        itkReportHeader.setAddressList(Arrays.asList(ADDRESS));
        MessageHeader messageHeader = messageHeaderService.createMessageHeader(itkReportHeader, MESSAGEID, EFFECTIVE_TIME);

        assertThat(messageHeader.getId()).isEqualTo(MESSAGEID);
        Coding event = messageHeader.getEvent();
        assertThat(event.getSystem()).isEqualTo(MessageHeaderEvent.SYSTEM);
        assertThat(event.getCode()).isEqualTo(DISCHARGE_DETAILS.getCode());
        assertThat(event.getDisplay()).isEqualTo(DISCHARGE_DETAILS.getDisplay());
        MessageSourceComponent source = messageHeader.getSource();
        assertThat(source.getName()).isEqualTo(MESSAGE_SOURCE_NAME);
        assertThat(source.getEndpoint()).isEqualTo(ENDPOINT);
        assertThat(messageHeader.getReason().getCodingFirstRep().getSystem()).isEqualTo(SPECIFICATION_KEY);
        assertThat(messageHeader.getReason().getCodingFirstRep().getCode()).isEqualTo(SPECIFICATION_VALUE);
        assertThat(messageHeader.getDestinationFirstRep().getEndpoint()).isEqualTo(ADDRESS);
        assertThat(messageHeader.getTimestamp()).isEqualTo(DateUtil.parseISODateTime(EFFECTIVE_TIME));
    }
}
