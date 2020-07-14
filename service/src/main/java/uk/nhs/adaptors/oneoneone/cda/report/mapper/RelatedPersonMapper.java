package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@AllArgsConstructor
public class RelatedPersonMapper {

    private final HumanNameMapper humanNameMapper;

    private final ContactPointMapper contactPointMapper;

    private final AddressMapper addressMapper;

    public RelatedPerson mapRelatedPerson(POCDMT000002UK01Informant12 informant, Encounter encounter) {
        if (!informant.isSetRelatedEntity()) {
            return null;
        }
        POCDMT000002UK01RelatedEntity relatedEntity = informant.getRelatedEntity();
        RelatedPerson relatedPerson = new RelatedPerson();

        relatedPerson.setIdElement(newRandomUuid());
        relatedPerson.setActive(true)
            .setPatient(encounter.getSubject())
            .setGender(Enumerations.AdministrativeGender.UNKNOWN);

        if (relatedEntity.isSetCode()) {
            if (relatedEntity.getCode().isSetDisplayName()) {
                relatedPerson.setRelationship(
                    new CodeableConcept().setText(
                        relatedEntity.getCode().getDisplayName()));
            }
        }

        if (relatedEntity.isSetRelatedPerson()) {
            relatedPerson.setName(getHumanNameFromITK(relatedEntity.getRelatedPerson()));
        }
        if (relatedEntity.sizeOfTelecomArray() > 0) {
            relatedPerson.setTelecom(getTelecomFromITK(relatedEntity.getTelecomArray()));
        }
        if (relatedEntity.sizeOfAddrArray() > 0) {
            relatedPerson.setAddress(getAddressesFromITK(relatedEntity.getAddrArray()));
        }

        if (relatedEntity.isSetEffectiveTime()) {
            Period period = new Period();
            if (relatedEntity.getEffectiveTime().isSetLow()) {
                period.setStart(DateUtil.parse(relatedEntity.getEffectiveTime().getLow().getValue()));
            }
            if (relatedEntity.getEffectiveTime().isSetHigh()) {
                period.setEnd(DateUtil.parse(relatedEntity.getEffectiveTime().getHigh().getValue()));
            }
            relatedPerson.setPeriod(period);
        }

        relatedPerson.setPeriod(getPeriod(relatedEntity));

        return relatedPerson;
    }

    private List<HumanName> getHumanNameFromITK(POCDMT000002UK01Person associatedPerson) {
        if (associatedPerson == null)
            return Collections.emptyList();
        PN[] itkPersonName = associatedPerson.getNameArray();
        return Arrays.stream(itkPersonName)
            .map(humanNameMapper::mapHumanName)
            .collect(Collectors.toList());
    }

    private List<ContactPoint> getTelecomFromITK(TEL[] itkTelecom) {
        return Arrays.stream(itkTelecom)
            .map(contactPointMapper::mapContactPoint)
            .collect(Collectors.toList());
    }

    private List<Address> getAddressesFromITK(AD[] itkAddressArray) {
        return Arrays.stream(itkAddressArray)
            .map(addressMapper::mapAddress)
            .collect(Collectors.toList());
    }

    private Period getPeriod(POCDMT000002UK01RelatedEntity relatedEntity) {
        if (relatedEntity == null || !relatedEntity.isSetEffectiveTime()) {
            return null;
        }

        Period period = new Period();
        IVLTS effectiveTime = relatedEntity.getEffectiveTime();
        if (effectiveTime.isSetLow()) {
            period.setStart(DateUtil.parse(effectiveTime.getLow().getValue()));
        }
        if (effectiveTime.isSetHigh()) {
            period.setEnd(DateUtil.parse(effectiveTime.getHigh().getValue()));
        }
        return period;
    }
}
