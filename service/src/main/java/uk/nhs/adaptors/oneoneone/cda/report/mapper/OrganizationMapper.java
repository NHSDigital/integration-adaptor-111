package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

@Component
@AllArgsConstructor
public class OrganizationMapper {

    private ContactPointMapper contactPointMapper;

    private AddressMapper addressMapper;

    public Organization mapOrganization(POCDMT000002UK01Organization itkOrganization) {
        Organization fhirOrganization = new Organization();
        fhirOrganization.setIdElement(newRandomUuid());
        fhirOrganization.setName(NodeUtil.getNodeValueString(itkOrganization.getNameArray(0)));
        fhirOrganization.setAddress(Arrays
            .stream(itkOrganization.getAddrArray())
            .map(addressMapper::mapAddress)
            .collect(Collectors.toList()));
        fhirOrganization.setTelecom(Arrays
            .stream(itkOrganization.getTelecomArray())
            .map(contactPointMapper::mapContactPoint)
            .collect(Collectors.toList()));
        if (itkOrganization.isSetStandardIndustryClassCode()) {
            fhirOrganization.setType(Collections.singletonList(new CodeableConcept()
                .setText(itkOrganization.getStandardIndustryClassCode().getDisplayName())));
        }
        if(itkOrganization.isSetAsOrganizationPartOf()) {
            Organization partOf = mapOrganization(itkOrganization);
            fhirOrganization.setPartOf(new Reference(partOf));
            fhirOrganization.setPartOfTarget(partOf);
        }
        return fhirOrganization;
    }
}
