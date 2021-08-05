package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.UUID;

import org.hl7.fhir.dstu3.model.IdType;

public class ResourceUtil {
    public static IdType newRandomUuid() {
        return new IdType(UUID.randomUUID().toString());
    }
}
