package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Practitioner;

public class PractitionerMapper {

    /*
    Practitioner - a person with a formal responsibility in the provisioning of healthcare or related services
     */

    public static Practitioner mapPractitioner(POCDMT000002UK01AssignedEntity assignedEntity) {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdElement(newRandomUuid());
        practitioner.setActive(true);
        practitioner.setName(getHumanNameFromITK(assignedEntity));
        practitioner.setTelecom(getTelecomFromITK(assignedEntity));
        practitioner.setAddress(getAddressesFromITK(assignedEntity));

        return practitioner;
    }

    private static List<HumanName> getHumanNameFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        PN[] itk_person_name = assignedEntity.getAssignedPerson().getNameArray();
        return Arrays.stream(itk_person_name)
            .map(HumanNameMapper::mapHumanName)
            .collect(Collectors.toList());
    }

    private static List<ContactPoint> getTelecomFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        TEL[] itk_telecom = assignedEntity.getTelecomArray();
        return Arrays.stream(itk_telecom)
            .map(ContactPointMapper::mapContactPoint)
            .collect(Collectors.toList());
    }

    private static List<Address> getAddressesFromITK(POCDMT000002UK01AssignedEntity assignedEntity) {
        AD[] itkAddressArray = assignedEntity.getAddrArray();
        return Arrays.stream(itkAddressArray)
            .map(AddressMapper::mapAddress)
            .collect(Collectors.toList());
    }

}
