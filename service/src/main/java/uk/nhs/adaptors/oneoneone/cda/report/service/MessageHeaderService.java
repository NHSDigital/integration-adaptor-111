package uk.nhs.adaptors.oneoneone.cda.report.service;

import static java.util.stream.Collectors.toList;

import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.text.ParseException;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MessageHeader;
import org.hl7.fhir.dstu3.model.MessageHeader.MessageDestinationComponent;
import org.hl7.fhir.dstu3.model.MessageHeader.MessageSourceComponent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkReportHeader;
import uk.nhs.adaptors.oneoneone.cda.report.enums.MessageHeaderEvent;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@Component
@RequiredArgsConstructor
public class MessageHeaderService {

    private static final String MESSAGE_SOURCE_NAME = "NHS 111 Adaptor";
    private static final String ITK_SPEC_PREFIX = "urn:nhs-itk:interaction:";

    private final SoapProperties soapProperties;

    public MessageHeader createMessageHeader(ItkReportHeader itkHeader, String messageId, String effectiveTime) throws ParseException {
        MessageHeader header = new MessageHeader();

        header.setIdElement(new IdType(messageId));
        header.setEvent(getEvent(itkHeader.getSpecVal()));
        header.setSource(getSource());
        header.setTimestamp(DateUtil.parseISODateTime(effectiveTime));
        header.setReason(new CodeableConcept().addCoding(
            new Coding()
                .setCode(itkHeader.getSpecVal())
                .setSystem(itkHeader.getSpecKey())));
        header.setDestination(itkHeader.getAddressList().stream()
            .map(it -> new MessageDestinationComponent()
                .setEndpoint(it))
            .collect(toList()));

        return header;
    }

    private MessageSourceComponent getSource() {
        MessageSourceComponent source = new MessageSourceComponent();
        source.setName(MESSAGE_SOURCE_NAME);
        source.setEndpoint(soapProperties.getSendTo());

        return source;
    }

    private Coding getEvent(String itkSpec) {
        String specValue = substringAfter(itkSpec, ITK_SPEC_PREFIX);
        if (specValue.startsWith("primary")) {
            return MessageHeaderEvent.REFERRAL.toCoding();
        } else if (specValue.startsWith("copy")) {
            return MessageHeaderEvent.DISCHARGE_DETAILS.toCoding();
        }
        return null;
    }
}
