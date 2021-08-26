package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.TS;

@Component
public class PeriodMapper {

    public Period mapPeriod(IVLTS ivlts) {
        DateTimeType high = DateUtil.parse(ivlts.getHigh().getValue());
        DateTimeType low = DateUtil.parse(ivlts.getLow().getValue());

        return new Period()
            .setStartElement(low)
            .setEndElement(high);
    }

    public Period mapPeriod(TS ts) {
        DateTimeType time = DateUtil.parse(ts.getValue());
        return new Period().setStartElement(time);
    }
}
