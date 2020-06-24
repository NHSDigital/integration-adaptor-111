package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;

import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GuardianMapper {

    private AddressMapper addressMapper;
    private HumanNameMapper humanNameMapper;
    private OrganizationMapper organizationMapper;

    public Patient.ContactComponent mapGuardian(POCDMT000002UK01Guardian guardian) {
        Patient.ContactComponent contactComponent = new Patient.ContactComponent();
        if (guardian.isSetGuardianPerson()) {
            POCDMT000002UK01Person guardianPerson = guardian.getGuardianPerson();
            if (guardianPerson.getNameArray().length > 0) {
                contactComponent.setName(humanNameMapper.mapHumanName(guardianPerson.getNameArray(0)));
            }
        }
        if (guardian.sizeOfAddrArray() > 0) {
            contactComponent.setAddress(addressMapper.mapAddress(guardian.getAddrArray(0)));
        }
        if (guardian.isSetGuardianOrganization()) {
            Organization managingOrganization = organizationMapper.mapOrganization(guardian.getGuardianOrganization());
            contactComponent.setOrganization(new Reference(managingOrganization));
            contactComponent.setOrganizationTarget(managingOrganization);
        }
        return contactComponent;
    }
}