package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;

@Component
@RequiredArgsConstructor
public class ItkAddressValidator {
    private static final String ODS_DOS_ID_VALIDATION_FAILED_MSG = "Message rejected";

    private final ItkProperties itkProperties;
    private final ReportItkHeaderParserUtil reportItkHeaderParserUtil;

    public void checkItkOdsAndDosId(Element itkHeader) throws SoapClientException {
        if (!isEmpty(itkProperties.getOdsCodes()) || !isEmpty(itkProperties.getDosIds())) {
            String odsCode = reportItkHeaderParserUtil.getOdsCode(itkHeader);
            String dosServiceId = reportItkHeaderParserUtil.getDosServiceId(itkHeader);

            boolean isOdsCodeCorrect = odsCode != null && itkProperties.getOdsCodes().contains(odsCode);
            boolean isDosIdCorrect = dosServiceId != null && itkProperties.getDosIds().contains(dosServiceId);

            if (!isOdsCodeCorrect && !isDosIdCorrect) {
                throw new SoapClientException(
                    String.format("Both ODS code (%s) and DOS ID (%s) are invalid", odsCode, dosServiceId),
                    ODS_DOS_ID_VALIDATION_FAILED_MSG
                );
            }
        }
    }
}
