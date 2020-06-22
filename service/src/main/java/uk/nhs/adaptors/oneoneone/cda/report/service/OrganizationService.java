package uk.nhs.adaptors.oneoneone.cda.report.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.OrganizationMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

@Component
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationMapper organizationMapper;

    private final FhirStorageService storageService;

    public Reference createOrganization(POCDMT000002UK01Organization docOrg) {
        Organization organization = organizationMapper.mapOrganization(docOrg);
        return storageService.create(organization);
    }
}
