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

    /*
    Practitioner - a person with a formal responsibility in the provisioning of healthcare or related services
     */

    public static Practitioner mapPractitioner(POCDMT000002UK01AssignedEntity assignedEntity) {
        Practitioner practitioner = new Practitioner();

        /*
        field:      id : Id
        purpose:    logical id of this artifact
        number:     0..1
         */
        practitioner.setId("");

        /*
        field:      meta : Meta
        purpose:    metadata about the resource
        number:     0..1
         */
        practitioner.setMeta(null);

        /*
        field:      implicitRules : Uri
        purpose:    a set of rules under which this content was created
        number:     0..1
         */
        practitioner.setImplicitRules("");

        /*
        field:      language : Code
        purpose:    language of the resource content (human language)
        number:     0..1
         */
        practitioner.setLanguage(null);

        /*
        field:      text : Narrative
        purpose:    text summary of the resource, for human interpretation
        number:     0..1
         */
        practitioner.setText(null);

        /*
        field:      extension (nhsCommunication) : Extension
        purpose:    NHS communication preferences for a resource
        number:     0..*
         */
        practitioner.setExtension(null);

        /*
        field:      modifierExtension : Extension
        purpose:    list of extensions that cannot be ignored
        number:     0..*
         */
        practitioner.setModifierExtension(null);

        /*
        field:      identifier : Identifier
        purpose:    an identifier for the person as this agent
        number:     0..*
         */
        practitioner.setIdentifier(getIdentifiersFromITK(assignedEntity));

        /*
        field:      identifier (sdsUserID) : Identifier
        purpose:    an identifier for the person as this agent
        number:     0..1
         */

        /*
        field:      identifier (sdsRoleProfileID) : Identifier
        purpose:    an identifier for the person as this agent
        number:     0..*
         */

        /*
        field:      active : Boolean
        purpose:    whether this practitioner's record is in active use
        number:     0..1
         */
        practitioner.setActive(true);

        /*
        field:      name : HumanName
        purpose:    the name(s) of this practitioner
        number:     0..*
         */
        practitioner.setName(getHumanNameFromITK(assignedEntity));

        /*
        field:      telecom : ContactPoint
        purpose:    contact details for that practitioner
        number:     0..*
         */
        practitioner.setTelecom(getTelecomFromITK(assignedEntity));

        /*
        field:      address : Address
        purpose:    Address(es) of the practitioner that are not role specific (typically home address)
        number:     0..*
         */
        practitioner.setAddress(getAddressesFromITK(assignedEntity));

        /*
        field:      gender : Code
        purpose:    male | female |other |unknown
        number:     0..1
         */
        practitioner.setGender(null);

        /*
        field:      birthDate : Date
        purpose:    birth date of practitioner
        number:     0..1
         */
        practitioner.setBirthDate(null);

        /*
        field:      photo : Attachment
        purpose:    image of the person
        number:     0..*
         */
        practitioner.setPhoto(getPhotoFromITK(assignedEntity));

        /*
        field:      qualification : BackboneElement
        purpose:    qualifications obtained by training and certification
        number:     0..*
         */
        practitioner.setQualification(getQualificationFromITK(assignedEntity));
        return practitioner;
    }

    private static List<Identifier> getIdentifiersFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Identifier> identifierList = new ArrayList<>();
        II itk_id = assignedEntity.getIdArray(1);
        identifierList.add(IdentifierMapper.mapIdentifier(itk_id));
        return identifierList;
    }

    private static List<HumanName> getHumanNameFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<HumanName> humanNameList = new ArrayList<>();
        EN itk_person_name = assignedEntity.getAssignedPerson().getNameArray(1);
        HumanName humanName = HumanNameMapper.mapHumanName(itk_person_name);
        humanNameList.add(humanName);
        return humanNameList;
    }

    private static List<ContactPoint> getTelecomFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<ContactPoint> contactPointList = new ArrayList<>();
        TEL itk_telecom = assignedEntity.getTelecomArray(1);
        ContactPoint contactPoint =  ContactPointMapper.mapContactPoint(itk_telecom);
        contactPointList.add(contactPoint);
        return contactPointList;
    }

    private static List<Address> getAddressesFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Address> addressList = new ArrayList<>();
        AD[] itkAddressArray = assignedEntity.getAddrArray();
        for (AD itkAddress : itkAddressArray) {
            addressList.add(AddressMapper.mapAddress(itkAddress));
        }
        return addressList;
    }

    private static List<Attachment> getPhotoFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(AttachmentMapper.mapAttachment());
        return attachmentList;
    }

    private static List<Practitioner.PractitionerQualificationComponent> getQualificationFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
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
        component.setIssuerTarget(getOrganization());
        return component;
    }

    private static Organization getOrganization() {
        return new Organization();
    }
}
