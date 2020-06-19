package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Patient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PatientMapper {

    private AddressMapper addressMapper;
    private ContactPointMapper contactPointMapper;
    private PeriodMapper periodMapper;
    private GuardianMapper gaurdianMapper;
    private HumanNameMapper humanNameMapper;

    public Patient mapPatient(POCDMT000002UK01PatientRole patientRole) {
        POCDMT000002UK01Patient itkPatient = patientRole.getPatient();

        Patient fhirPatient = new Patient();
        fhirPatient.setIdElement(IdType.newRandomUuid());
        fhirPatient.setActive(true);
        fhirPatient.setName(getNames(itkPatient));
        fhirPatient.setAddress(getAddresses(patientRole));
        fhirPatient.setTelecom(getContactPoints(patientRole));
        fhirPatient.setContact(getContactComponents(itkPatient));
        fhirPatient.setExtension(getExtensions(itkPatient));

        if (itkPatient.isSetBirthTime()) {
            fhirPatient.setBirthDate(periodMapper.mapPeriod(itkPatient.getBirthTime()).getStart());
        }
        if (itkPatient.isSetAdministrativeGenderCode()) {
            fhirPatient.setGender(Enumerations.AdministrativeGender
                .fromCode(itkPatient.getAdministrativeGenderCode().getCode()));
        }
        if (itkPatient.isSetMaritalStatusCode()) {
            fhirPatient.setMaritalStatus(getMaritalStatus(itkPatient));
        }
        return fhirPatient;
    }

    private List<Address> getAddresses(POCDMT000002UK01PatientRole patientRole) {
        if (patientRole.sizeOfAddrArray() > 0) {
            return Arrays.stream(patientRole.getAddrArray())
                .map(addressMapper::mapAddress).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<ContactPoint> getContactPoints(POCDMT000002UK01PatientRole patientRole) {
        if (patientRole.sizeOfTelecomArray() > 0) {
            return Arrays.stream(patientRole.getTelecomArray())
                .map(contactPointMapper::mapContactPoint)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<HumanName> getNames(POCDMT000002UK01Patient itkPatient) {
        if (itkPatient.sizeOfNameArray() > 0) {
            return Arrays.stream(itkPatient.getNameArray())
                .map(humanNameMapper::mapHumanName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private CodeableConcept getMaritalStatus(POCDMT000002UK01Patient itkPatient) {
        CodeableConcept maritalStatus = new CodeableConcept();
        maritalStatus.setText(itkPatient.getMaritalStatusCode().getDisplayName());
        return maritalStatus;
    }

    private List<Patient.ContactComponent> getContactComponents(POCDMT000002UK01Patient itkPatient) {
        if (itkPatient.sizeOfGuardianArray() > 0) {
            return Arrays.stream(itkPatient.getGuardianArray())
                    .map(gaurdianMapper::mapGuardian)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<Extension> getExtensions(POCDMT000002UK01Patient itkPatient) {
        List<Extension> extensionList = new ArrayList<>();
        if (itkPatient.isSetEthnicGroupCode()) {
            extensionList.add(createExtension("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-EthnicCategory-1",
                itkPatient.getEthnicGroupCode().getCode()));
        }
        if (itkPatient.isSetReligiousAffiliationCode()) {
            extensionList.add(createExtension("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-ReligiousAffiliation-1",
                itkPatient.getReligiousAffiliationCode().getCode()));
        }
        if (itkPatient.isSetBirthplace()) {
            extensionList.add(createExtension("http://hl7.org/fhir/StructureDefinition/birthPlace",
                itkPatient.getBirthplace().toString()));
        }
        return extensionList;
    }

    private Extension createExtension(String strUrl, String id) {
        Extension extension = new Extension();
        if (!strUrl.isEmpty()) {
            extension.setUrl(strUrl);
        }
        StringType stringType = new StringType();
        stringType.setId(id);
        extension.setValue(stringType);

        return extension;
    }
}