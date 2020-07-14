package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.nio.file.Files.readAllBytes;

import java.net.URL;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class ItkResponseUtil {

    private static final String SUCCESS_TEMPLATE = loadSuccessTemplate();
    private static final String SOAP_ITK_OK_RESPONSE_TEMPLATE_PATH = "/itk/SoapItkOkResponseTemplate.xml";

    public String createSuccessResponseEntity(String originalMessageId, String responseMessageId) {
        return String.format(SUCCESS_TEMPLATE, responseMessageId, originalMessageId);
    }

    private static String loadSuccessTemplate() {
        return getResourceAsString(SOAP_ITK_OK_RESPONSE_TEMPLATE_PATH);
    }

    private static String getResourceAsString(String path) {
        try {
            URL reportXmlResource = ItkResponseUtil.class.getResource(path);
            return new String(readAllBytes(Paths.get(reportXmlResource.getPath())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
