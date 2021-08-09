package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

@Component
@AllArgsConstructor
public class OrganizationMapper {

    private final ContactPointMapper contactPointMapper;

    private final AddressMapper addressMapper;

    private final NodeUtil nodeUtil;

    private final ResourceUtil resourceUtil;

    public Organization mapOrganization(POCDMT000002UK01Organization itkOrganization) {
        Organization fhirOrganization = new Organization();
        fhirOrganization.setIdElement(resourceUtil.newRandomUuid());
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
            if (itkOrganization.getAsOrganizationPartOf().getWholeOrganization() != null) {
                Organization partOf = mapOrganization(itkOrganization.getAsOrganizationPartOf().getWholeOrganization());
                fhirOrganization.setPartOf(new Reference(partOf));
                fhirOrganization.setPartOfTarget(partOf);
            }
        }
        if (itkOrganization.sizeOfIdArray() > 0) {
            List<Identifier> identifierList = new ArrayList<>();
            for (II id : itkOrganization.getIdArray()) {
                if (id.isSetExtension()) {
                    identifierList.add(new Identifier().setValue(id.getExtension()));
                }
            }
            fhirOrganization.setIdentifier(identifierList);
        }

        return fhirOrganization;
    }
}
