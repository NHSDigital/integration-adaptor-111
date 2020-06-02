package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Period;

public class ContactPointMapper {

    public static ContactPoint mapContactPoint(TEL itk_telecom) {
        ContactPoint contactPoint =  new ContactPoint();
        contactPoint.setSystem(null);
        contactPoint.setValue(itk_telecom.getValue());
        contactPoint.setUse(ContactPoint.ContactPointUse.fromCode(itk_telecom.getUse().get(1).toString()));
        contactPoint.setRank(0);
        contactPoint.setPeriod(new Period());
        return contactPoint;
    }
}
