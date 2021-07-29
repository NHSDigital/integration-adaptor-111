package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Enumerations.*;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.*;

import java.util.Map;

public class GenderMapper {

    private static final Map<String, AdministrativeGender> mapping = Map.of("1", MALE, "2", FEMALE, "9", OTHER);

    public static AdministrativeGender getGenderFromCode(String genderCode) {
        if (genderCode == null) {
            return mapping.getOrDefault(0, UNKNOWN);
        } else {
            return mapping.getOrDefault(genderCode, UNKNOWN);
        }
    }
}
