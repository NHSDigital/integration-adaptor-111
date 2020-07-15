package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class DateUtilTest {
    private final SimpleDateFormat isoDateformatter = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

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

    @Test
    public void shouldParseAnISODateFormatCorrectly() throws ParseException {
        String dateAsString = "20120102";
        assertThat(DateUtil.parseISODate(dateAsString)).isEqualTo(isoDateformatter.parse("20120102"));
    }

    @Test
    public void shouldParseAnISODateTimeFormatCorrectly() {
        String dateAsString = "201201021234+00";
        assertThat(DateUtil.parseISODateTime(dateAsString)).isEqualTo(Date.from(Instant.parse("2012-01-02T12:34:00.00Z")));
    }

    @Test
    public void shouldThrowExceptionForWrongISODateFormat() {
        String dateAsString = "30/01/2020";
        Assertions.assertThrows(IllegalStateException.class, () -> DateUtil.parseISODate(dateAsString));
    }

    @Test
    public void shouldThrowExceptionForWrongISODateTimeFormat() {
        String dateAsString = "30/02/19891898.00";
        Assertions.assertThrows(IllegalStateException.class, () -> DateUtil.parseISODateTime(dateAsString));
    }
}
