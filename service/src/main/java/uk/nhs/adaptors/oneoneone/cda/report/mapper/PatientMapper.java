package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.adaptors.oneoneone.cda.report.enums.MaritalStatus;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@RequiredArgsConstructor
public class PatientMapper {

    private final AddressMapper addressMapper;
    private final HumanNameMapper humanNameMapper;
    private final ContactPointMapper contactPointMapper;
    private final OrganizationMapper organizationMapper;
    public Patient mapPatient(POCDMT000002UK01PatientRole patientRole) {

        var fhirPatient = new Patient();

        fhirPatient.setIdElement(newRandomUuid());

        Stream.of(patientRole.getAddrArray())
                .map(addressMapper::mapAddress)
                .forEach(fhirPatient::addAddress);

        Stream.of(patientRole.getTelecomArray())
                .map(contactPointMapper::mapContactPoint)
                .forEach(fhirPatient::addTelecom);

        if (patientRole.isSetProviderOrganization()) {
            fhirPatient.addGeneralPractitioner(
                    new Reference(organizationMapper.mapOrganization(patientRole.getProviderOrganization())));
        }

        var patientElement = patientRole.getPatient();
        if (patientElement != null) {
            Stream.of(patientElement.getNameArray())
                    .map(humanNameMapper::mapHumanName)
                    .forEach(fhirPatient::addName);

            Stream.of(patientElement.getLanguageCommunicationArray())
                    .map(this::getLanguageCommunicationCode)
                    .forEach(fhirPatient::setLanguage);

            if (patientElement.isSetAdministrativeGenderCode()) {
                var genderCode = patientElement.getAdministrativeGenderCode();
                fhirPatient.setGender(Enumerations.AdministrativeGender.fromCode(
                        genderCode.getDisplayName().toLowerCase()));
            }

            if (patientElement.isSetMaritalStatusCode()) {
                var maritalStatusCode = patientElement.getMaritalStatusCode().getCode();
                var maritalStatus = MaritalStatus.fromCode(maritalStatusCode).toCodeableConcept();
                fhirPatient.setMaritalStatus(maritalStatus);
            }

            if (patientElement.isSetBirthTime()) {
                String dob = patientElement.getBirthTime().getValue();
                StringBuilder str = new StringBuilder(dob);
                str.insert(6,'/');
                str.insert(4,'/');
                Date date1= null;
                try {
                    date1 = new SimpleDateFormat("yyyy/MM/dd").parse(str.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                fhirPatient.setBirthDate(date1);
            }
        }

        return fhirPatient;
    }

    private String getLanguageCommunicationCode(POCDMT000002UK01LanguageCommunication languageCommunication){
        return languageCommunication.getLanguageCode().getCode();
    }

}
