package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;
import org.junit.Test;

public class DateUtilTest {

    private static final String[] DATES_AS_STRINGS = {
        "20120102123421Z",
        "201201021234X",
        "20120102"
    };

    private static final Date[] EXPECTED_DATES = {
        Date.from(Instant.parse("2012-01-02T12:34:21.00Z")),
        Date.from(Instant.parse("2012-01-02T12:34:00.00Z")),
        Date.from(Instant.parse("2012-01-02T00:00:00.00Z")),
    };

    @Test
    public void parse() {
        for (int i = 0; i < 3; i++){
            assertThat(DateUtil.parse(DATES_AS_STRINGS[i])).isEqualTo(EXPECTED_DATES[i]);
        }
    }

}
