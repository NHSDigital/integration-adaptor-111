package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedCustodian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Custodian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01CustodianOrganization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ServiceProviderMapper {

    private AddressMapper addressMapper;

    private ContactPointMapper contactPointMapper;

    public Organization mapServiceProvider(POCDMT000002UK01Custodian custodian) {
        POCDMT000002UK01AssignedCustodian assignedCustodian = custodian.getAssignedCustodian();
        POCDMT000002UK01CustodianOrganization custodianOrganization = assignedCustodian.getRepresentedCustodianOrganization();

        Organization serviceProviderOrganization = new Organization();
        serviceProviderOrganization.setIdElement(newRandomUuid());
        serviceProviderOrganization.setActive(true);

        if (custodianOrganization.isSetAddr()) {
            serviceProviderOrganization.setAddress(Collections
                .singletonList(addressMapper.mapAddress(custodianOrganization.getAddr())));
        }

        if (custodianOrganization.isSetTelecom()) {
            serviceProviderOrganization.setTelecom(Collections
                .singletonList(contactPointMapper.mapContactPoint(custodianOrganization.getTelecom())));
        }

        if (custodian.isSetTypeCode()) {
            serviceProviderOrganization.setType(retrieveTypeFromITK(custodian));
        }

        if(custodianOrganization.isSetName()) {
            serviceProviderOrganization.setName(NodeUtil.getNodeValueString(custodianOrganization.getName()));
        }

        return serviceProviderOrganization;
    }

    private List<CodeableConcept> retrieveTypeFromITK(POCDMT000002UK01Custodian custodian) {
        return Collections.singletonList(new CodeableConcept()
            .setText(custodian.getTypeCode()));
    }
}
