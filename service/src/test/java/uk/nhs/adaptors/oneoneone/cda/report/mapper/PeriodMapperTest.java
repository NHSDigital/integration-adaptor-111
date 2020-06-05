package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;

import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.IVXBTS;

import org.hl7.fhir.dstu3.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PeriodMapperTest {

    @InjectMocks
    private PeriodMapper periodMapper;

    private  static final String PERIOD_START_ITK = "201105191945+00";
    private static final String PERIOD_END_ITK = "201105192015+00";

    @Test
    public void mapPeriod() {

        IVXBTS ivxbtsLow = IVXBTS.Factory.newInstance();
        ivxbtsLow.setValue(PERIOD_START_ITK);

        IVXBTS ivxbtsHigh = IVXBTS.Factory.newInstance();
        ivxbtsHigh.setValue(PERIOD_END_ITK);

        IVLTS ivlts = IVLTS.Factory.newInstance();
        ivlts.setLow(ivxbtsLow);
        ivlts.setHigh(ivxbtsHigh);

        Period period = periodMapper.mapPeriod(ivlts);

        assertThat(period.getStart()).isEqualTo(Date.from(Instant.parse("2011-05-19T19:45:00.00Z")));
        assertThat(period.getEnd()).isEqualTo(Date.from(Instant.parse("2011-05-19T20:15:00.00Z")));
    }

}
