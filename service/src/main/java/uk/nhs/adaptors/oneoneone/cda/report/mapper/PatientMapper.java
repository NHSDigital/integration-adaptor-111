package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Guardian;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Patient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PatientMapper {
    private AddressMapper addressMapper;
    private ContactPointMapper contactPointMapper;
    private PeriodMapper periodMapper;
    private GaurdianMapper gaurdianMapper;

    public Patient mapPatient(POCDMT000002UK01PatientRole patientRole) {
        Patient fhirPatient = new Patient();

        List<Address> addressList = new ArrayList<>();
        if (patientRole.sizeOfAddrArray() > 0) {
            for (AD itkAddress : patientRole.getAddrArray()) {
                Address address = addressMapper.mapAddress(itkAddress);
                addressList.add(address);
            }
        }
        fhirPatient.setAddress(addressList);

        patientRole.getTelecomArray();
        List<ContactPoint> contactPoints = new ArrayList<>();
        if (patientRole.sizeOfTelecomArray() > 0) {
            for (TEL itkTel : patientRole.getTelecomArray()) {
                ContactPoint contactPoint = contactPointMapper.mapContactPoint(itkTel);
                contactPoints.add(contactPoint);
            }
        }
        fhirPatient.setTelecom(contactPoints);

        POCDMT000002UK01Patient itkPatient = patientRole.getPatient();

        CE aGenderCode = itkPatient.getAdministrativeGenderCode();
        fhirPatient.setGender(Enumerations.AdministrativeGender.fromCode(aGenderCode.getCode()));

        fhirPatient.setBirthDate(periodMapper.mapPeriod(itkPatient.getBirthTime()).getStart());

        CodeableConcept cConcept = new CodeableConcept();
        cConcept.setText(itkPatient.getMaritalStatusCode().getDisplayName());
        fhirPatient.setMaritalStatus(cConcept);

        List<Patient.ContactComponent> contactComponents = new ArrayList<>();
        if (itkPatient.sizeOfGuardianArray() > 0) {
            Arrays.stream(itkPatient.getGuardianArray())
                    .map(gaurdianMapper::mapGuardian)
                    .collect(Collectors.toList());
        }
        fhirPatient.setContact(contactComponents);

        //START extensions
        List<Extension> extensionList = new ArrayList<>();
        extensionList.add(createExtension("https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-EthnicCategory-1",
                                          itkPatient.getEthnicGroupCode().getCode()));
        extensionList.add(createExtension(null, itkPatient.getReligiousAffiliationCode().getCode()));
        extensionList.add(createExtension(null, itkPatient.getBirthplace().toString()));
        fhirPatient.setExtension(extensionList);
        //END extensions

        return fhirPatient;
    }

    private Extension createExtension(String strUrl, String id) {
        Extension extension = new Extension();
        if (!strUrl.isEmpty()) extension.setUrl(strUrl);

        StringType stringType = new StringType();
        stringType.setId(id);
        extension.setValue(stringType);

        return extension;
    }
}