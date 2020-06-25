package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Organization;

import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01LanguageCommunication;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Patient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class PatientMapper {

    private final AddressMapper addressMapper;
    private final ContactPointMapper contactPointMapper;
    private final PeriodMapper periodMapper;
    private final GuardianMapper guardianMapper;
    private final HumanNameMapper humanNameMapper;
    private final OrganizationMapper orgMapper;

    public Patient mapPatient(POCDMT000002UK01PatientRole patientRole) {
        Patient fhirPatient = new Patient();
        fhirPatient.setIdElement(newRandomUuid());
        if (patientRole.isSetPatient()) {
            POCDMT000002UK01Patient itkPatient = patientRole.getPatient();
            fhirPatient.setActive(true);
            fhirPatient.setName(getNames(itkPatient));
            fhirPatient.setAddress(getAddresses(patientRole));
            fhirPatient.setTelecom(getContactPoints(patientRole));

            fhirPatient.addGeneralPractitioner(getGeneralPractioner(patientRole));

            if (itkPatient.sizeOfLanguageCommunicationArray() > 0) {
                Stream.of(itkPatient.getLanguageCommunicationArray())
                    .map(this::getLanguageCommunicationCode)
                    .forEach(fhirPatient::setLanguage);
            }

            fhirPatient.setContact(getContactComponents(itkPatient));
            fhirPatient.setExtension(getExtensions(itkPatient));

            if (itkPatient.isSetBirthTime()) {
                fhirPatient.setBirthDate(periodMapper.mapPeriod(itkPatient.getBirthTime()).getStart());
            }

            if (itkPatient.isSetAdministrativeGenderCode()) {
                String displayName = itkPatient.getAdministrativeGenderCode().getDisplayName();
                fhirPatient.setGender(Enumerations.AdministrativeGender
                        .fromCode(displayName == null ? "unknown" : displayName.toLowerCase()));
            }

            if (itkPatient.isSetMaritalStatusCode()) {
                fhirPatient.setMaritalStatus(getMaritalStatus(itkPatient));
            }
        }
        return fhirPatient;
    }

    private Reference getGeneralPractioner(POCDMT000002UK01PatientRole patientRole) {
        Reference reference = null;
        if (patientRole.isSetProviderOrganization()) {
            Organization organization = orgMapper.mapOrganization(patientRole.getProviderOrganization());
            reference = new Reference(organization);
        }
        return reference;
    }

    private String getLanguageCommunicationCode(POCDMT000002UK01LanguageCommunication languageCommunication){
        return languageCommunication.getLanguageCode().getCode();
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
        maritalStatus.setText(itkPatient.getMaritalStatusCode().getCode());
        return maritalStatus;
    }

    private List<Patient.ContactComponent> getContactComponents(POCDMT000002UK01Patient itkPatient) {
        if (itkPatient.sizeOfGuardianArray() > 0) {
            return Arrays.stream(itkPatient.getGuardianArray())
                    .map(guardianMapper::mapGuardian)
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
            String birthPlace = NodeUtil.getNodeValueString(itkPatient.getBirthplace().getPlace().getName());
            extensionList.add(createExtension("http://hl7.org/fhir/StructureDefinition/birthPlace",
                    birthPlace));
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