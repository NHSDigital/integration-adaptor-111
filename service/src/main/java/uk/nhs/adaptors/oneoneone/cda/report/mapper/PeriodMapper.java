package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.TS;

import java.util.Date;

@Component
public class PeriodMapper {

    public Period mapPeriod(IVLTS ivlts) {
        Date high = DateUtil.parse(ivlts.getHigh().getValue());
        Date low = DateUtil.parse(ivlts.getLow().getValue());

        return new Period()
                .setStart(low)
                .setEnd(high);
    }

    public Period mapPeriod(TS ts) {
        Date time = DateUtil.parse(ts.getValue());
        return new Period().setStart(time);
    }
}
