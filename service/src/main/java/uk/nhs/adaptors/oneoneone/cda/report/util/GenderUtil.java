package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Enumerations;

public class GenderUtil {

    static Map<String, Enumerations.AdministrativeGender> mapping = new HashMap<>();
    static{
        mapping.put("1", Enumerations.AdministrativeGender.MALE);
        mapping.put("2", Enumerations.AdministrativeGender.FEMALE);
        mapping.put("9", Enumerations.AdministrativeGender.OTHER);
    }
    public static Enumerations.AdministrativeGender getGenderFromCode(String genderCode){
        return mapping.getOrDefault(genderCode,Enumerations.AdministrativeGender.UNKNOWN);
    }
}
