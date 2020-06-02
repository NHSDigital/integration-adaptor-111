package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import uk.nhs.connect.iucds.cda.ucr.TEL;

import org.hl7.fhir.dstu3.model.ContactPoint;

public class ContactPointMapper {

    public static ContactPoint mapContactPoint(TEL itk_telecom) {
        ContactPoint contactPoint =  new ContactPoint();

        if (itk_telecom.sizeOfUseablePeriodArray() > 0) {
            contactPoint.setPeriod(PeriodMapper.mapPeriod(itk_telecom.getUseablePeriodArray(1)));
        }

        if (itk_telecom.isSetUse()) {
            contactPoint.setUse(ContactPoint.ContactPointUse.fromCode(itk_telecom.getUse().get(1).toString()));
        }



        contactPoint.setSystem(null);
        contactPoint.setValue(itk_telecom.getValue());

        contactPoint.setRank(0);
        return contactPoint;
    }
}
