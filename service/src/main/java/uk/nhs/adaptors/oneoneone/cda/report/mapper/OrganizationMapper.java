package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Organization;

public class OrganizationMapper {

    public static Organization mapOrganization(POCDMT000002UK01Organization itk_organization) {
        Organization organization = new Organization();
        organization.setId("");
        organization.setMeta(null);
        organization.setImplicitRules(null);
        organization.setLanguage(null);
        organization.setText(null);
        organization.setContained(null);
        organization.setExtension(null);
        organization.setModifierExtension(null);
        organization.setIdentifier(Collections.singletonList(IdentifierMapper.mapIdentifier(itk_organization.getIdArray(0))));
        organization.setType(Collections.singletonList(new CodeableConcept(new Coding(itk_organization.getTypeId().toString(), "", ""))));
        organization.setTelecom(Collections.singletonList(ContactPointMapper.mapContactPoint(itk_organization.getTelecomArray(1))));
        organization.setAddress(Collections.singletonList(AddressMapper.mapAddress(itk_organization.getAddrArray(1))));
        organization.setPartOf(null);
        organization.setContact(null);
        organization.setEndpoint(null);

        return  organization;
    }
}
