package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ItkResponseUtil {

    private static final String SOAP_ITK_OK_RESPONSE_TEMPLATE_PATH = "itk/SoapItkOkResponseTemplate.xml";
    private static final String SUCCESS_TEMPLATE = loadSuccessTemplate();
    private static final String SOAP_ITK_ERROR_RESPONSE_TEMPLATE_PATH = "itk/SoapItkErrorResponseTemplate.xml";
    private static final String UNSUCCESSFUL_TEMPLATE = loadUnsuccessTemplate();
    private static final String SOAP_ITK_ERROR_RESPONSE_BAD_REQUEST_TEMPLATE_PATH = "itk/SoapItkErrorResponseTemplateBadRequest.xml";
    private static final String UNSUCCESSFUL_BAD_REQUEST_TEMPLATE = loadUnsuccessBadRequestTemplate();

    private static String loadSuccessTemplate() {
        return getResourceAsString(SOAP_ITK_OK_RESPONSE_TEMPLATE_PATH);
    }

    private static String getResourceAsString(String path) {
        try {
            return IOUtils.toString(new ClassPathResource(path).getInputStream(), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String loadUnsuccessTemplate() {
        return getResourceAsString(SOAP_ITK_ERROR_RESPONSE_TEMPLATE_PATH);
    }

    private static String loadUnsuccessBadRequestTemplate() {
        return getResourceAsString(SOAP_ITK_ERROR_RESPONSE_BAD_REQUEST_TEMPLATE_PATH);
    }

    public String createSuccessResponseEntity(String originalMessageId, String responseMessageId) {
        return String.format(SUCCESS_TEMPLATE, responseMessageId, originalMessageId);
    }

    public String createUnSuccessfulResponseEntity(String originalMessageId, String address, String responseMessageId, String errorCode,
        String errorId, String errorForUser, String technicalDetailsOfError) {
        return String.format(UNSUCCESSFUL_TEMPLATE, originalMessageId, address, responseMessageId, errorId, errorCode, errorForUser,
            technicalDetailsOfError);
    }

    public String createUnSuccessfulResponseEntityBadRequest(String originalMessageId, String responseMessageId, String errorCode,
        String errorId, String errorForUser, String technicalDetailsOfError) {
        return String.format(UNSUCCESSFUL_BAD_REQUEST_TEMPLATE, originalMessageId, responseMessageId, errorCode, errorId, errorForUser,
            technicalDetailsOfError);
    }
}
