package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssociatedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@AllArgsConstructor
public class PractitionerMapper {

    private final HumanNameMapper humanNameMapper;

    private final ContactPointMapper contactPointMapper;

    private final AddressMapper addressMapper;
    private final ResourceUtil resourceUtil;

    public Practitioner mapPractitioner(POCDMT000002UK01AssociatedEntity associatedEntity) {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdElement(resourceUtil.newRandomUuid());
        practitioner.setActive(true);
        practitioner.setName(getHumanNameFromITK(associatedEntity.getAssociatedPerson()));
        practitioner.setTelecom(getTelecomFromITK(associatedEntity.getTelecomArray()));
        practitioner.setAddress(getAddressesFromITK(associatedEntity.getAddrArray()));

        return practitioner;
    }

    private List<HumanName> getHumanNameFromITK(POCDMT000002UK01Person associatedPerson) {
        if (associatedPerson == null) {
            return Collections.emptyList();
        }
        PN[] itkPersonName = associatedPerson.getNameArray();
        return stream(itkPersonName)
            .map(humanNameMapper::mapHumanName)
            .collect(toList());
    }

    private List<ContactPoint> getTelecomFromITK(TEL[] itkTelecom) {
        return stream(itkTelecom)
            .map(contactPointMapper::mapContactPoint)
            .collect(toList());
    }

    private List<Address> getAddressesFromITK(AD[] itkAddressArray) {
        return stream(itkAddressArray)
            .map(addressMapper::mapAddress)
            .collect(toList());
    }

    public Practitioner mapPractitioner(POCDMT000002UK01AssignedEntity assignedEntity) {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdElement(resourceUtil.newRandomUuid());
        practitioner.setActive(true);
        if (assignedEntity.isSetAssignedPerson()) {
            practitioner.setName(getHumanNameFromITK(assignedEntity.getAssignedPerson()));
        }
        practitioner.setTelecom(getTelecomFromITK(assignedEntity.getTelecomArray()));
        practitioner.setAddress(getAddressesFromITK(assignedEntity.getAddrArray()));

        return practitioner;
    }

    public Practitioner mapPractitioner(POCDMT000002UK01AssignedAuthor assignedAuthor) {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdElement(resourceUtil.newRandomUuid());
        practitioner.setActive(true);
        practitioner.setName(getHumanNameFromITK(assignedAuthor.getAssignedPerson()));
        practitioner.setTelecom(getTelecomFromITK(assignedAuthor.getTelecomArray()));
        practitioner.setAddress(getAddressesFromITK(assignedAuthor.getAddrArray()));

        return practitioner;
    }
}
