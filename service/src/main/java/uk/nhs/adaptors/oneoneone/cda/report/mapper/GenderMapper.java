package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.FEMALE;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.MALE;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.OTHER;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.UNKNOWN;

import java.util.Map;

public class GenderMapper {

    private static final Map<String, AdministrativeGender> MAPPING = Map.of("1", MALE, "2", FEMALE, "9", OTHER);

    public static AdministrativeGender getGenderFromCode(String genderCode) {
        if (genderCode == null) {
            return UNKNOWN;
        }
        return MAPPING.getOrDefault(genderCode, UNKNOWN);
    }
}
