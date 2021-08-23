package uk.nhs.adaptors.oneoneone.cda.report.util;

import static java.util.Locale.ENGLISH;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class DateUtilTest {
    private final SimpleDateFormat isoDateformatter = new SimpleDateFormat("yyyyMMdd", ENGLISH);

    @Test
    public void shouldParseForCorrectDateFormat() {
        String dateAsString = "201201021234+00";
        assertThat(DateUtil.parse(dateAsString)).isEqualTo(Date.from(Instant.parse("2012-01-02T12:34:00.00Z")));
    }

    @Test
    public void shouldThrowExceptionForEmptyString() {
        String dateAsString = "";
        assertThrows(IllegalStateException.class, () -> DateUtil.parse(dateAsString));
    }

    @Test
    public void shouldThrowExceptionForIncorrectDateFormat() {
        String dateAsString = "202019891898.00";
        assertThrows(IllegalStateException.class, () -> DateUtil.parse(dateAsString));
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
        assertThrows(IllegalStateException.class, () -> DateUtil.parseISODate(dateAsString));
    }

    @Test
    public void shouldThrowExceptionForWrongISODateTimeFormat() {
        String dateAsString = "30/02/19891898.00";
        assertThrows(IllegalStateException.class, () -> DateUtil.parseISODateTime(dateAsString));
    }

    @ParameterizedTest(name = "parsePathwaysDate")
    @MethodSource("pathwaysDates")
    public void shouldParsePathwaysDateFormatCorrectly(String inputString, String parsedDate) {
        Date pathwaysDate = DateUtil.parsePathwaysDate(inputString);
        assertThat(toIsoDateTimeString(pathwaysDate)).isEqualTo(parsedDate);
    }

    private static Stream<Arguments> pathwaysDates() {
        return Stream.of(
            Arguments.of(
                "2011-02-17T17:31:14.313+01:00",
                "2011-02-17T16:31:14.313Z"
            ),
            Arguments.of(
                "2015-06-11T16:22:44.959Z",
                "2015-06-11T16:22:44.959Z"
            ),
            Arguments.of(
                "2017-01-22T03:21:33.443+00:00",
                "2017-01-22T03:21:33.443Z"
            ),
            Arguments.of(
                "2018-07-24T18:51:41.854-07:00",
                "2018-07-25T01:51:41.854Z"
            )
        );
    }

    @Test
    public void shouldThrowExceptionForWrongPathwaysDateFormat() {
        String dateAsString = "30/01/2020";
        assertThrows(DateTimeParseException.class, () -> DateUtil.parsePathwaysDate(dateAsString));
    }
}
