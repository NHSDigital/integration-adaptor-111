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

    @Test
    public void shouldCreateUnSuccessResponseEntity() {
        String unSuccessResponseEntity = itkResponseUtil.createUnSuccessfulResponseEntity("123", "address", "456",
            "1000", "789", "error for user", "technical details of error");
        String expectedResponse = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:a=\"http://www.w3" +
            ".org/2005/08/addressing\"\n" +
            "            xmlns:itk=\"urn:nhs-itk:ns:201005\">\n" +
            "    <s:Header>\n" +
            "        <a:MessageID>456</a:MessageID>\n" +
            "        <a:Action>http://www.w3.org/2005/08/addressing/soap/fault</a:Action>\n" +
            "        <a:To>address</a:To>\n" +
            "        <a:RelatesTo>123</a:RelatesTo>\n" +
            "    </s:Header>\n" +
            "    <s:Body>\n" +
            "        <s:Fault>\n" +
            "            <faultcode>s:Client</faultcode>\n" +
            "            <faultstring>A client related error has occurred, see detail element for further information</faultstring>\n" +
            "            <faultactor>http://source.of.fault.example.com</faultactor>\n" +
            "            <detail>\n" +
            "                <itk:ToolkitErrorInfo>\n" +
            "                    <itk:ErrorID>1000</itk:ErrorID>\n" +
            "                    <itk:ErrorCode codeSystem=\"2.16.840.1.113883.2.1.3.2.4.17" +
            ".268\">789</itk:ErrorCode>\n" +
            "                    <itk:ErrorText>error for user</itk:ErrorText>\n" +
            "                    <itk:ErrorDiagnosticText>technical details of error</itk:ErrorDiagnosticText>\n" +
            "                </itk:ToolkitErrorInfo>\n" +
            "            </detail>\n" +
            "        </s:Fault>\n" +
            "    </s:Body>\n" +
            "</s:Envelope>";
        assertThat(unSuccessResponseEntity).isEqualTo(expectedResponse);
    }
}
