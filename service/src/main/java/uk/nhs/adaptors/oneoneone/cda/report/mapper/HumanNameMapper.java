package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Collections;

import uk.nhs.connect.iucds.cda.ucr.EN;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.StringType;

public class HumanNameMapper {

    public static HumanName mapHumanName(EN itk_person_name) {
        HumanName humanName = new HumanName();
        humanName.setUse(null);
        humanName.setText(null);
        humanName.setFamily(itk_person_name.getFamilyArray(1).toString());
        humanName.setGiven(Collections.singletonList(new StringType(itk_person_name.getGivenArray(1).toString())));
        humanName.setPrefix(Collections.singletonList(new StringType(itk_person_name.getPrefixArray(1).toString())));
        humanName.setSuffix(Collections.singletonList(new StringType(itk_person_name.getSuffixArray(1).toString())));
        humanName.setPeriod(new Period());
        return humanName;
    }
}
