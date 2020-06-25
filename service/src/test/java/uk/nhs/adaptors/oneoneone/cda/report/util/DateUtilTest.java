package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilTest {

    @Test
    public void shouldParseForCorrectDateFormat() {
        String dateAsString = "201201021234+00";
        assertThat(DateUtil.parse(dateAsString)).isEqualTo(Date.from(Instant.parse("2012-01-02T12:34:00.00Z")));
    }

    @Test
    public void shouldThrowExceptionForEmptyString() {
        String dateAsString = "";
        Assertions.assertThrows(IllegalStateException.class, () -> DateUtil.parse(dateAsString));
    }

    @Test
    public void shouldThrowExceptionForIncorrectDateFormat() {
        String dateAsString = "202019891898.00";
        Assertions.assertThrows(IllegalStateException.class, () -> DateUtil.parse(dateAsString));
    }

}
