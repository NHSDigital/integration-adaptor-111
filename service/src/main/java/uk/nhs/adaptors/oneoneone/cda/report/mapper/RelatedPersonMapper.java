package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.UNKNOWN;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Person;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@AllArgsConstructor
public class RelatedPersonMapper {

    private static final String EMERGENCY_CONTACT_CODE = "C";
    private static final String EMERGENCY_CONTACT_DISPLAY = "Emergency Contact";
    private static final String EMERGENCY_CONTACT_SYSTEM = "http://hl7.org/fhir/v2/0131";
    private static final String ITK_EMERGENCY_TELECOM_USE = "EC";

    private final HumanNameMapper humanNameMapper;

    private final ContactPointMapper contactPointMapper;

    private final AddressMapper addressMapper;

    private final ResourceUtil resourceUtil;

    public RelatedPerson mapRelatedPerson(POCDMT000002UK01Informant12 informant, Encounter encounter) {
        if (!informant.isSetRelatedEntity()) {
            return null;
        }
        POCDMT000002UK01RelatedEntity relatedEntity = informant.getRelatedEntity();
        RelatedPerson relatedPerson = new RelatedPerson();

        relatedPerson.setIdElement(resourceUtil.newRandomUuid());
        relatedPerson.setActive(true)
            .setPatient(encounter.getSubject())
            .setGender(UNKNOWN);

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
        setRelationship(relatedEntity, relatedPerson);
        markEmergencyContact(relatedEntity.getTelecomArray(), relatedPerson);

        return relatedPerson;
    }

    public RelatedPerson createEmergencyContactRelatedPerson(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        RelatedPerson relatedPerson = new RelatedPerson();

        TEL[] telecomArray = Optional.ofNullable(clinicalDocument)
            .map(document -> document.getRecordTargetArray(0))
            .map(POCDMT000002UK01RecordTarget::getPatientRole)
            .map(POCDMT000002UK01PatientRole::getTelecomArray)
            .orElse(null);

        if (getEmergencyTelecom(telecomArray).isPresent()) {
            relatedPerson.setIdElement(resourceUtil.newRandomUuid());
            relatedPerson.setPatient(encounter.getSubject());
            relatedPerson.setTelecom(getTelecomFromITK(new TEL[] {getEmergencyTelecom(telecomArray).get()}));
            markEmergencyContact(telecomArray, relatedPerson);

            return relatedPerson;
        }

        return null;
    }

    private void setRelationship(POCDMT000002UK01RelatedEntity relatedEntity, RelatedPerson relatedPerson) {
        if (relatedEntity.isSetCode()) {
            CE code = relatedEntity.getCode();
            Coding coding = new Coding()
                .setCode(code.getCode())
                .setDisplay(code.getDisplayName())
                .setSystem(code.getCodeSystem());
            relatedPerson.setRelationship(new CodeableConcept(coding));
        }
    }

    private void markEmergencyContact(TEL[] telecomArray, RelatedPerson relatedPerson) {
        getEmergencyTelecom(telecomArray)
            .ifPresent(it -> {
                Coding coding = new Coding()
                    .setCode(EMERGENCY_CONTACT_CODE)
                    .setDisplay(EMERGENCY_CONTACT_DISPLAY)
                    .setSystem(EMERGENCY_CONTACT_SYSTEM);
                if (relatedPerson.hasRelationship()) {
                    relatedPerson.getRelationship().addCoding(coding);
                } else {
                    relatedPerson.setRelationship(new CodeableConcept(coding));
                }
            });
    }

    private Optional<TEL> getEmergencyTelecom(TEL[] telecomArray) {
        return stream(telecomArray)
            .filter(it -> !isEmpty(it.getUse()))
            .filter(it -> it.getUse().contains(ITK_EMERGENCY_TELECOM_USE))
            .findFirst();
    }

    private List<HumanName> getHumanNameFromITK(POCDMT000002UK01Person associatedPerson) {
        if (associatedPerson == null) {
            return emptyList();
        }
        PN[] itkPersonName = associatedPerson.getNameArray();
        return stream(itkPersonName)
            .map(humanNameMapper::mapHumanName)
            .collect(Collectors.toList());
    }

    private List<ContactPoint> getTelecomFromITK(TEL[] itkTelecom) {
        return stream(itkTelecom)
            .map(contactPointMapper::mapContactPoint)
            .collect(Collectors.toList());
    }

    private List<Address> getAddressesFromITK(AD[] itkAddressArray) {
        return stream(itkAddressArray)
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
