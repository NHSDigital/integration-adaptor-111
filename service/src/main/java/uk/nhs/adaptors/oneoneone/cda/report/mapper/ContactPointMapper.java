package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.HashMap;
import java.util.Map;

import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.ContactPoint;

public class ContactPointMapper {

    private static Map<String, ContactPoint.ContactPointUse> map = new HashMap<>();

    static {
        map.put("H", ContactPoint.ContactPointUse.HOME);
        map.put("HV", ContactPoint.ContactPointUse.HOME);
        map.put("MC", ContactPoint.ContactPointUse.MOBILE);
        map.put("EC", ContactPoint.ContactPointUse.TEMP);
        map.put("WP", ContactPoint.ContactPointUse.WORK);
    }

    public static ContactPoint mapContactPoint(TEL itk_telecom) {
        ContactPoint contactPoint =  new ContactPoint();

        if (itk_telecom.sizeOfUseablePeriodArray() > 0) {
            contactPoint.setPeriod(PeriodMapper.mapPeriod(itk_telecom.getUseablePeriodArray(0)));
        }

        if (itk_telecom.isSetUse()) {
            contactPoint.setUse(map.get(itk_telecom.getUse().get(0).toString()));
        }

        if (itk_telecom.isSetValue()) {
            contactPoint.setValue(itk_telecom.getValue());
        }

        return contactPoint;
    }
}
