package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.UUID;

import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceUtil {

    public IdType newRandomUuid() {
        return new IdType(UUID.randomUUID().toString());
    }
}
