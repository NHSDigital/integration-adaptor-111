package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.enums.MaritalStatus;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01LanguageCommunication;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Patient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;

@Component
@AllArgsConstructor
public class PatientMapper {

    private static final String NHS_NUMBER_VERIFIED_OID = "2.16.840.1.113883.2.1.4.1";
    private static final String NHS_NUMBER_UNVERIFIED_OID = "2.16.840.1.113883.2.1.3.2.4.18.23";

    private static final String NHS_FHIR_ID_SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";
    private static final String NHS_VERIFICATION_STATUS =
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-NHSNumberVerificationStatus-1";
    private final AddressMapper addressMapper;
    private final ContactPointMapper contactPointMapper;
    private final PeriodMapper periodMapper;
    private final GuardianMapper guardianMapper;
    private final HumanNameMapper humanNameMapper;
    private final OrganizationMapper orgMapper;
    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public Patient mapPatient(POCDMT000002UK01PatientRole patientRole) {
        Patient fhirPatient = new Patient();
        fhirPatient.setIdElement(resourceUtil.newRandomUuid());
        fhirPatient.setIdentifier(getNhsNumbers(patientRole));
        fhirPatient.setActive(true);
        if (patientRole.isSetPatient()) {
            POCDMT000002UK01Patient itkPatient = patientRole.getPatient();
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
                fhirPatient.setGender(GenderMapper.getGenderFromCode(itkPatient.getAdministrativeGenderCode().getCode()));
            }

            if (itkPatient.isSetMaritalStatusCode()) {
                if (itkPatient.getMaritalStatusCode().isSetCode()) {
                    MaritalStatus.fromCode(itkPatient.getMaritalStatusCode().getCode()).ifPresent(maritalStatus -> {
                        Coding coding = new Coding();
                        if (!StringUtils.isBlank(maritalStatus.getCode())) {
                            coding.setCode(maritalStatus.getCode());
                        }
                        if (!StringUtils.isBlank(maritalStatus.getDisplay())) {
                            coding.setDisplay(maritalStatus.getDisplay());
                        }
                        if (!StringUtils.isBlank(maritalStatus.getSystem())) {
                            coding.setSystem(maritalStatus.getSystem());
                        }
                        if (coding.hasDisplay() || coding.hasSystem() || coding.hasCode()) {
                            fhirPatient.setMaritalStatus(new CodeableConcept(coding));
                        }
                    });
                }
            }
        }
        return fhirPatient;
    }

    private List<Identifier> getNhsNumbers(POCDMT000002UK01PatientRole patientRole) {
        return stream(patientRole.getIdArray())
            .filter(it -> asList(NHS_NUMBER_VERIFIED_OID, NHS_NUMBER_UNVERIFIED_OID).contains(it.getRoot()))
            .map(it -> {
                Identifier identifier = new Identifier()
                    .setValue(it.getExtension())
                    .setSystem(NHS_FHIR_ID_SYSTEM);
                identifier.setExtension(getNhsNumberExtension(it));
                return identifier;
            })
            .collect(toList());
    }

    private List<Extension> getNhsNumberExtension(II it) {
        Extension extension = new Extension(NHS_VERIFICATION_STATUS);
        Coding code = new Coding()
            .setSystem(NHS_VERIFICATION_STATUS)
            .setCode(mapNhsNumberVerificationStatus(it));
        extension.setValue(new CodeableConcept(code));
        return asList(extension);
    }

    private String mapNhsNumberVerificationStatus(II it) {
        switch (it.getRoot()) {
            case NHS_NUMBER_VERIFIED_OID:
                return "01";
            case NHS_NUMBER_UNVERIFIED_OID:
                return "02";
            default:
                return null;
        }
    }

    private List<HumanName> getNames(POCDMT000002UK01Patient itkPatient) {
        if (itkPatient.sizeOfNameArray() > 0) {
            return stream(itkPatient.getNameArray())
                .map(humanNameMapper::mapHumanName).collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<Address> getAddresses(POCDMT000002UK01PatientRole patientRole) {
        if (patientRole.sizeOfAddrArray() > 0) {
            return stream(patientRole.getAddrArray())
                .map(addressMapper::mapAddress).collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private List<ContactPoint> getContactPoints(POCDMT000002UK01PatientRole patientRole) {
        if (patientRole.sizeOfTelecomArray() > 0) {
            return stream(patientRole.getTelecomArray())
                .map(contactPointMapper::mapContactPoint)
                .collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Reference getGeneralPractioner(POCDMT000002UK01PatientRole patientRole) {
        Reference reference = null;
        if (patientRole.isSetProviderOrganization()) {
            Organization organization = orgMapper.mapOrganization(patientRole.getProviderOrganization());
            reference = new Reference(organization);
        }
        return reference;
    }

    private String getLanguageCommunicationCode(POCDMT000002UK01LanguageCommunication languageCommunication) {
        return languageCommunication.getLanguageCode().getCode();
    }

    private List<Patient.ContactComponent> getContactComponents(POCDMT000002UK01Patient itkPatient) {
        if (itkPatient.sizeOfGuardianArray() > 0) {
            return stream(itkPatient.getGuardianArray())
                .map(guardianMapper::mapGuardian)
                .collect(toList());
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
            extensionList.add(createExtension("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect"
                    + "-ReligiousAffiliation-1",
                itkPatient.getReligiousAffiliationCode().getCode()));
        }
        if (itkPatient.isSetBirthplace()) {
            String birthPlace = nodeUtil.getNodeValueString(itkPatient.getBirthplace().getPlace().getName());
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