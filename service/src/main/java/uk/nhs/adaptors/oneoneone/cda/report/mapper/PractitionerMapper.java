package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.List;

import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.EN;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;

public class PractitionerMapper {

    public static Practitioner mapPractitioner(POCDMT000002UK01AssignedEntity assignedEntity) {
        Practitioner practitioner = new Practitioner();
        practitioner.setId("");
        practitioner.setMeta(null);
        practitioner.setImplicitRules("");
        practitioner.setLanguage(null);
        practitioner.setText(null);
        practitioner.setContained(null);
        practitioner.setExtension(null);
        practitioner.setIdentifier(retrieveIdentifiersFromITK(assignedEntity));
        practitioner.setActive(true);
        practitioner.setName(retrieveHumanNameFromITK(assignedEntity));
        practitioner.setTelecom(retrieveTelecomFromITK(assignedEntity));
        practitioner.setAddress(retrieveAddressesFromITK(assignedEntity));
        practitioner.setGender(null);
        practitioner.setBirthDate(null);
        practitioner.setPhoto(retrievePhotoFromITK(assignedEntity));
        practitioner.setQualification(retrieveQualificationFromITK(assignedEntity));
        return practitioner;
    }

    private static List<Identifier> retrieveIdentifiersFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Identifier> identifierList = new ArrayList<>();
        II itk_id = assignedEntity.getIdArray(1);
        identifierList.add(IdentifierMapper.mapIdentifier(itk_id));
        return identifierList;
    }

    private static List<HumanName> retrieveHumanNameFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<HumanName> humanNameList = new ArrayList<>();
        EN itk_person_name = assignedEntity.getAssignedPerson().getNameArray(1);
        HumanName humanName = HumanNameMapper.mapHumanName(itk_person_name);
        humanNameList.add(humanName);
        return humanNameList;
    }

    private static List<ContactPoint> retrieveTelecomFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<ContactPoint> contactPointList = new ArrayList<>();
        TEL itk_telecom = assignedEntity.getTelecomArray(1);
        ContactPoint contactPoint =  ContactPointMapper.mapContactPoint(itk_telecom);
        contactPointList.add(contactPoint);
        return contactPointList;
    }

    private static List<Address> retrieveAddressesFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Address> addressList = new ArrayList<>();
        AD[] itkAddressArray = assignedEntity.getAddrArray();
        for (AD itkAddress : itkAddressArray) {
            addressList.add(AddressMapper.mapAddress(itkAddress));
        }
        return addressList;
    }

    private static List<Attachment> retrievePhotoFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(AttachmentMapper.mapAttachment());
        return attachmentList;
    }

    private static List<Practitioner.PractitionerQualificationComponent> retrieveQualificationFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Practitioner.PractitionerQualificationComponent> list = new ArrayList<>();
        list.add(PractitionerQualificationComponentMapper.mapPractitionerQualificationComponent());
        return list;
    }

}

class PractitionerQualificationComponentMapper {

    static Practitioner.PractitionerQualificationComponent mapPractitionerQualificationComponent(){
        Practitioner.PractitionerQualificationComponent component = new Practitioner.PractitionerQualificationComponent();
        component.setModifierExtension(null);
        component.setIdentifier(null);
        component.setCode(null);
        component.setPeriod(null);
        component.setIssuer(null);
        component.setIssuerTarget(retrieveOrganization());
        return component;
    }

    private static Organization retrieveOrganization() {
        return new Organization();
    }
}
