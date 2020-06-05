package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.HashMap;
import java.util.Map;

import uk.nhs.connect.iucds.cda.ucr.TEL;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.springframework.stereotype.Component;

@Component
public class ContactPointMapper {

    private PeriodMapper periodMapper;

    private static final Map<String, ContactPoint.ContactPointUse> CONTACT_POINT_USE_MAP = new HashMap<>();

    static {
        CONTACT_POINT_USE_MAP.put("H", ContactPoint.ContactPointUse.HOME);
        CONTACT_POINT_USE_MAP.put("HV", ContactPoint.ContactPointUse.HOME);
        CONTACT_POINT_USE_MAP.put("MC", ContactPoint.ContactPointUse.MOBILE);
        CONTACT_POINT_USE_MAP.put("EC", ContactPoint.ContactPointUse.TEMP);
        CONTACT_POINT_USE_MAP.put("WP", ContactPoint.ContactPointUse.WORK);
    }

    public ContactPointMapper(PeriodMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    public ContactPoint mapContactPoint(TEL itkTelecom) {
        ContactPoint contactPoint =  new ContactPoint();

        if (itkTelecom.sizeOfUseablePeriodArray() > 0) {
            contactPoint.setPeriod(periodMapper.mapPeriod(itkTelecom.getUseablePeriodArray(0)));
        }

        if (itkTelecom.isSetUse()) {
            contactPoint.setUse(CONTACT_POINT_USE_MAP.get(itkTelecom.getUse().get(0).toString()));
        }

        if (itkTelecom.isSetValue()) {
            contactPoint.setValue(itkTelecom.getValue());
        }

        return contactPoint;
    }
}
