package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.EMAIL;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.OTHER;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.PHONE;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.HOME;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.MOBILE;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.TEMP;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.WORK;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@AllArgsConstructor
public class ContactPointMapper {

    private static final Map<String, ContactPointUse> CONTACT_POINT_USE_MAP = new HashMap<>();
    private static final Map<String, ContactPointSystem> CONTACT_POINT_SYSTEM_MAP = new HashMap<>();
    private static final String PHONE_SYSTEM_PREFIX = "tel";
    private static final String EMAIL_SYSTEM_PREFIX = "mailto";

    static {
        CONTACT_POINT_USE_MAP.put("H", HOME);
        CONTACT_POINT_USE_MAP.put("HV", HOME);
        CONTACT_POINT_USE_MAP.put("MC", MOBILE);
        CONTACT_POINT_USE_MAP.put("EC", TEMP);
        CONTACT_POINT_USE_MAP.put("WP", WORK);

        CONTACT_POINT_SYSTEM_MAP.put(PHONE_SYSTEM_PREFIX, PHONE);
        CONTACT_POINT_SYSTEM_MAP.put(EMAIL_SYSTEM_PREFIX, EMAIL);
    }

    private final PeriodMapper periodMapper;

    public ContactPoint mapContactPoint(TEL itkTelecom) {
        ContactPoint contactPoint = new ContactPoint();

        if (itkTelecom.sizeOfUseablePeriodArray() > 0) {
            contactPoint.setPeriod(periodMapper.mapPeriod(itkTelecom.getUseablePeriodArray(0)));
        }

        if (itkTelecom.isSetUse()) {
            contactPoint.setUse(CONTACT_POINT_USE_MAP.get(itkTelecom.getUse().get(0).toString()));
        }

        if (itkTelecom.isSetValue()) {
            String telecomValue = itkTelecom.getValue();
            contactPoint.setValue(telecomValue);
            if (telecomValue.startsWith(PHONE_SYSTEM_PREFIX)) {
                contactPoint.setSystem(CONTACT_POINT_SYSTEM_MAP.get(PHONE_SYSTEM_PREFIX));
            } else if (telecomValue.startsWith(EMAIL_SYSTEM_PREFIX)) {
                contactPoint.setSystem(CONTACT_POINT_SYSTEM_MAP.get(EMAIL_SYSTEM_PREFIX));
            } else {
                contactPoint.setSystem(OTHER);
            }
        }

        return contactPoint;
    }
}
