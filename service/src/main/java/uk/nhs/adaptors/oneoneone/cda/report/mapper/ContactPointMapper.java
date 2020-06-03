package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.apachecommons.CommonsLog;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactPointMapper {

    @Autowired
    PeriodMapper periodMapper;

    private static Map<String, ContactPoint.ContactPointUse> map = new HashMap<>();

    static {
        map.put("H", ContactPoint.ContactPointUse.HOME);
        map.put("HV", ContactPoint.ContactPointUse.HOME);
        map.put("MC", ContactPoint.ContactPointUse.MOBILE);
        map.put("EC", ContactPoint.ContactPointUse.TEMP);
        map.put("WP", ContactPoint.ContactPointUse.WORK);
    }

    public ContactPoint mapContactPoint(TEL itkTelecom) {
        ContactPoint contactPoint =  new ContactPoint();

        if (itkTelecom.sizeOfUseablePeriodArray() > 0) {
            contactPoint.setPeriod(periodMapper.mapPeriod(itkTelecom.getUseablePeriodArray(0)));
        }

        if (itkTelecom.isSetUse()) {
            contactPoint.setUse(map.get(itkTelecom.getUse().get(0).toString()));
        }

        if (itkTelecom.isSetValue()) {
            contactPoint.setValue(itkTelecom.getValue());
        }

        return contactPoint;
    }
}
