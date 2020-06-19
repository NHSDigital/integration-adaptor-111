package uk.nhs.adaptors.oneoneone.cda.report.mapper;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import org.hl7.fhir.dstu3.model.Patient;

public class GaurdianMapper {
    private AddressMapper addressMapper;

    private OrganizationMapper organizationMapper;

    public Patient.ContactComponent mapGuardian(POCDMT000002UK01Guardian guardian) {
        Patient.ContactComponent contactComponent = new Patient.ContactComponent();
        contactComponent.setAddress(addressMapper.mapAddress(guardian.getAddrArray(0)));
        Organization managingOrganization = organizationMapper.mapOrganization(guardian.getGuardianOrganization());
        contactComponent.setOrganization(new Reference(managingOrganization));
        contactComponent.setOrganizationTarget(managingOrganization);

        return contactComponent;
    }
}
