package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.List;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.RelatedPerson;

public class RelatedPersonMapper {

    public static RelatedPerson mapRelatedPerson(POCDMT000002UK01AssignedEntity assignedEntity) {
        RelatedPerson relatedPerson = new RelatedPerson();

        /*
        field:      id : Id
        purpose:    logical id of this artifact
        number:     0..1
        usage:      this will always be populated except when the resource is being created (initial creation call)
         */
        relatedPerson.setId("");

        /*
        field:      meta : Meta
        purpose:    metadata about the resource
        number:     0..1
         */
        relatedPerson.setMeta(null);

        /*
        field:      implicitRules : Uri
        purpose:    a set of rules under which this content was created
        number:     0..1
         */
        relatedPerson.setImplicitRules("");

        /*
        field:      language : Code
        purpose:    language of the resource content (human language)
        number:     0..1
         */
        relatedPerson.setLanguage(null);

        /*
        field:      text : Narrative
        purpose:    text summary of the resource, for human interpretation
        number:     0..1
         */
        relatedPerson.setText(null);

        /*
        field:      extension : Extension
        purpose:    additional content defined by implementations
        number:     0..*
         */
        relatedPerson.setExtension(null);

        /*
        field:      modifierExtension : Extension
        purpose:    list of extensions that cannot be ignored
        number:     0..*
         */
        relatedPerson.setModifierExtension(null);

        /*
        field:      identifier : Identifier
        purpose:    an identifier for the person as this agent
        number:     0..*
         */
        relatedPerson.setIdentifier(getIdentifiersFromITK(assignedEntity));

        /*
        field:      active : Boolean
        purpose:    whether this practitioner's record is in active use
        number:     0..1
         */
        relatedPerson.setActive(true);

        /*
        MANDATORY
        field:      patient : Reference (Patient)
        purpose:    the patient this person is related to
        number:     1
         */
        relatedPerson.setPatient(null);

        /*
        field:      relationship : CodeableConcept
        purpose:    The nature of this relationship PatientRelationshipType (Preferred)
        number:     0..1
         */

        /*
        field:      name : HumanName
        purpose:    the name(s) of this person
        number:     0..*
         */
        relatedPerson.setName(getHumanNameFromITK(assignedEntity));

        /*
        field:      telecom : ContactPoint
        purpose:    contact details for that practitioner
        number:     0..*
         */
        relatedPerson.setTelecom(getTelecomFromITK(assignedEntity));

        /*
        field:      gender : Code
        purpose:    male | female |other |unknown
        number:     0..1
         */
        relatedPerson.setGender(null);

        /*
        field:      birthDate : Date
        purpose:    birth date of practitioner
        number:     0..1
         */
        relatedPerson.setBirthDate(null);

        /*
        field:      address : Address
        purpose:    Address(es) of the practitioner that are not role specific (typically home address)
        number:     0..*
         */
        relatedPerson.setAddress(getAddressesFromITK(assignedEntity));

        /*
        field:      photo : Attachment
        purpose:    image of the person
        number:     0..*
         */
        relatedPerson.setPhoto(getPhotoFromITK(assignedEntity));

        /*
        field:      qualification : BackboneElement
        purpose:    period of time that this relationship is considered valid
        number:     0..1
         */

        return relatedPerson;
    }

    private static List<Attachment> getPhotoFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        return null;
    }

    private static List<Address> getAddressesFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        return null;
    }

    private static List<ContactPoint> getTelecomFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        return null;
    }

    private static List<HumanName> getHumanNameFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        return null;
    }

    private static List<Identifier> getIdentifiersFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        return null;
    }
}
