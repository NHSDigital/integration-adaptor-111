package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Date;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.MessageHeader;
import org.hl7.fhir.dstu3.model.MessageHeader.MessageSourceComponent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@Component
@RequiredArgsConstructor
public class MessageHeaderService {

    private static final String EVENT_SYSTEM = "https://fhir.nhs.uk/STU3/CodeSystem/ITK-MessageEvent-2";
    private static final String EVENT_CODE = "ITK007C";
    private static final String EVENT_DISPLAY_VALUE = "ITK GP Connect Send Document";
    private static final String MESSAGE_SOURCE_NAME = "NHS 111 Adaptor";

    private final SoapProperties soapProperties;

    public MessageHeader createMessageHeader(String specificationKey, String specificationValue) {
        MessageHeader header = new MessageHeader();

        header.setIdElement(newRandomUuid());
        header.setId(header.getIdElement());
        header.setEvent(getEvent());
        header.setSource(getSource());
        header.setTimestamp(new Date());
        header.setReason(new CodeableConcept().addCoding(
            new Coding()
                .setCode(specificationValue)
                .setSystem(specificationKey)));

        return header;
    }

    private MessageSourceComponent getSource() {
        MessageSourceComponent source = new MessageSourceComponent();
        source.setName(MESSAGE_SOURCE_NAME);
        source.setEndpoint(soapProperties.getSendTo());

        return source;
    }

    private Coding getEvent() {
        return new Coding()
            .setSystem(EVENT_SYSTEM)
            .setCode(EVENT_CODE)
            .setDisplay(EVENT_DISPLAY_VALUE);
    }
}
