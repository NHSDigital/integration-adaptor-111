package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.UUID;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceUtil {

    public IdType newRandomUuid() {
        return new IdType(UUID.randomUUID().toString());
    }

    public Reference createReference(Resource resource) {
        return new Reference(resource).setReferenceElement(new IdType("urn:uuid:" + resource.getId()));
    }
}
