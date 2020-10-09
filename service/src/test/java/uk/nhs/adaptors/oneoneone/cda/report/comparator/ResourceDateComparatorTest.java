package uk.nhs.adaptors.oneoneone.cda.report.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourceDateComparatorTest {

    private static final Date NOW = new Date();
    private static final Date LATER = Date.from(NOW.toInstant().plusSeconds(60));
    private static final ReferralRequest FIRST = new ReferralRequest().setAuthoredOn(NOW);
    private static final ReferralRequest SECOND = new ReferralRequest().setAuthoredOn(LATER);

    private ResourceDateComparator resourceDateComparator;

    @BeforeEach
    private void setUp() {
        resourceDateComparator = new ResourceDateComparator();
    }

    @Test
    public void shouldReturnFirstForValidDates() {
        assertThat(resourceDateComparator.compare(FIRST, SECOND)).isEqualTo(1);
    }

    @Test
    public void shouldReturnFirstForValidDateAndNull() {
        assertThat(resourceDateComparator.compare(FIRST, null)).isEqualTo(1);
    }

    @Test
    public void shouldReturnEqualForValidDate() {
        assertThat(resourceDateComparator.compare(FIRST, FIRST)).isEqualTo(0);
    }

    @Test
    public void shouldReturnSecondForValidDateAndNull() {
        assertThat(resourceDateComparator.compare(null, SECOND)).isEqualTo(-1);
    }

    @Test
    public void shouldReturnEqualForNull() {
        assertThat(resourceDateComparator.compare(null, null)).isEqualTo(0);
    }
}
