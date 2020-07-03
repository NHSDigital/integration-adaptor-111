package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@AllArgsConstructor
public class OrganizationMapper {

    private final ContactPointMapper contactPointMapper;

    private final AddressMapper addressMapper;

    private final NodeUtil nodeUtil;

    public Organization mapOrganization(POCDMT000002UK01Organization itkOrganization) {
        Organization fhirOrganization = new Organization();
        fhirOrganization.setIdElement(newRandomUuid());
        fhirOrganization.setName(nodeUtil.getNodeValueString(itkOrganization.getNameArray(0)));
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

        if (itkOrganization.isSetAsOrganizationPartOf()) {
            Organization partOf = mapOrganization(itkOrganization);
            fhirOrganization.setPartOf(new Reference(partOf));
            fhirOrganization.setPartOfTarget(partOf);
        }
        return fhirOrganization;
    }

}
