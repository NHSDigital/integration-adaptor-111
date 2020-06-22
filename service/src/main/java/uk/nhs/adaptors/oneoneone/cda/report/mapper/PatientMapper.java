package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {
    //TODO
    // This is just a placeholder for the mapper. EpisodeOfCare requires a reference to a patient for message to be valid

    public Patient mapPatient() {
        Patient patient = new Patient();
        patient.setId(newRandomUuid());
        return patient;
    }
}

