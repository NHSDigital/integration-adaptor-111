package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ItkResponseUtilTest {
    private final ItkResponseUtil itkResponseUtil = new ItkResponseUtil();

    @Test
    public void shouldCreateSuccessResponseEntity() {
        String successResponseEntity = itkResponseUtil.createSuccessResponseEntity("123", "234");
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas"
            + ".xmlsoap.org/soap/envelope/\""
            + " xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsu=\"http://docs.oasis-open"
            + ".org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""
            + " xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0"
            + ".xsd\">\n\t<soap:Header>\n\t\t<wsa:MessageID>234</wsa:MessageID>"
            + "\n\t\t<wsa:Action>urn:nhs-itk:services:201005:SendNHS111Report-v2-0Response</wsa:Action>\n\t</soap:Header>\n\t<soap:Body"
            + "><itk"
            + ":SimpleMessageResponse "
            + "xmlns:itk=\"urn:nhs-itk:ns:201005\">OK:123</itk:SimpleMessageResponse>\n</soap:Body>\n</soap:Envelope>\n";
        assertThat(successResponseEntity).isEqualTo(expectedResponse);
    }
}
