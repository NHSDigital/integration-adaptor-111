package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;

@Component
@AllArgsConstructor
public class GuardianMapper {

    private final AddressMapper addressMapper;
    private final HumanNameMapper humanNameMapper;
    private final OrganizationMapper organizationMapper;
    private final ResourceUtil resourceUtil;

    public Patient.ContactComponent mapGuardian(POCDMT000002UK01Guardian guardian) {
        Patient.ContactComponent contactComponent = new Patient.ContactComponent();
        if (guardian.isSetGuardianPerson()) {
            POCDMT000002UK01Person guardianPerson = guardian.getGuardianPerson();
            if (guardianPerson.sizeOfNameArray() > 0) {
                contactComponent.setName(humanNameMapper.mapHumanName(guardianPerson.getNameArray(0)));
            }
        }
        if (guardian.sizeOfAddrArray() > 0) {
            contactComponent.setAddress(addressMapper.mapAddress(guardian.getAddrArray(0)));
        }
        if (guardian.isSetGuardianOrganization()) {
            Organization managingOrganization = organizationMapper.mapOrganization(guardian.getGuardianOrganization());
            contactComponent.setOrganization(resourceUtil.createReference(managingOrganization));
            contactComponent.setOrganizationTarget(managingOrganization);
        }
        return contactComponent;
    }
}