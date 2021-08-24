package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

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
    @ParameterizedTest(name = "parseDate")
    @MethodSource("dates")
    public void shouldParseForCorrectDateFormat(String inputString, String expectedDate) {
        assertThat(DateUtil.parse(inputString).asStringValue()).isEqualTo(expectedDate);
    }

    private static Stream<Arguments> dates() {
        return Stream.of(
            Arguments.of(
                "2011",
                "2011"
            ),
            Arguments.of( // TODO jak to naprawic?
                "201503",
                "2015-03"
            ),
            Arguments.of(
                "20170122",
                "2017-01-22"
            ),
            Arguments.of(
                "2018072518",
                "2018-07-25T17:00:00+00:00"
            ),
            Arguments.of(
                "201807251820",
                "2018-07-25T17:20:00+00:00"
            ),
            Arguments.of(
                "20180725182021",
                "2018-07-25T17:20:21+00:00"
            ),
            Arguments.of(
                "20180625182021+01",
                "2018-06-25T17:20:21+00:00"
            ),
            Arguments.of(
                "201808251820+01",
                "2018-08-25T17:20:00+00:00"
            ),
            Arguments.of(
                "2019072518+01",
                "2019-07-25T17:00:00+00:00"
            ),
            Arguments.of(
                "20170725182021+0100",
                "2017-07-25T17:20:21+00:00"
            ),
            Arguments.of(
                "20170725182021+0130",
                "2017-07-25T16:50:21+00:00"
            ),
            Arguments.of(
                "201802251820+0100",
                "2018-02-25T17:20:00+00:00"
            ),
            Arguments.of(
                "2019122518+0100",
                "2019-12-25T17:00:00+00:00"
            )
        );
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
