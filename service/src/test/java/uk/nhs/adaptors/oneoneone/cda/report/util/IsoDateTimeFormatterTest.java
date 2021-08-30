package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

public final class IsoDateTimeFormatterTest {

    @Test
    public void shouldParseDateToIsoDateTimeString() {
        Date date = Date.from(Instant.parse("2020-09-03T12:07:58Z"));
        assertThat(IsoDateTimeFormatter.toIsoDateTimeString(date)).isEqualTo("2020-09-03T12:07:58.000Z");
    }
}
