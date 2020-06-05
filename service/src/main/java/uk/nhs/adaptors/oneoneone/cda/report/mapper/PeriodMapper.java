package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Date;

import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;

import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;

@Component
public class PeriodMapper {

    public Period mapPeriod(IVLTS ivlts) {
        Date high = DateUtil.parse(ivlts.getHigh().getValue());
        Date low = DateUtil.parse(ivlts.getLow().getValue());

        return new Period()
            .setStart(low)
            .setEnd(high);
    }
}
