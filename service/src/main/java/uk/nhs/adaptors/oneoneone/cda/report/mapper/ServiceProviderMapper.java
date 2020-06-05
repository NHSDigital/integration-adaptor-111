package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

@Component
public class ServiceProviderMapper {

    public Organization mapServiceProvider() {
        Organization organization = new Organization();
        organization.setIdElement(newRandomUuid());

        return organization;
    }
}
