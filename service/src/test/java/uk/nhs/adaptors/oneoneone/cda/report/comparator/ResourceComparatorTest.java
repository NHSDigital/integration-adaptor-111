package uk.nhs.adaptors.oneoneone.cda.report.comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResourceComparatorTest {
    @InjectMocks
    private ResourceComparator resourceComparator;
    @Mock
    private ReferralRequest referralRequest1;
    @Mock
    private ReferralRequest referralRequest2;

    @Test
    public void shouldReturnNegativeOneForValidDates() {
        mockReferralRequest1();
        mockReferralRequest2();

        assertThat(resourceComparator.compare(referralRequest1, referralRequest2)).isEqualTo(-1);
    }

    private void mockReferralRequest1() {
        Date now = new Date();

        when(referralRequest1.getAuthoredOn()).thenReturn(now);
        when(referralRequest1.getResourceType()).thenReturn(ResourceType.ReferralRequest);
    }

    @SuppressWarnings("MagicNumber")
    private void mockReferralRequest2() {
        Date after = Date.from(new Date().toInstant().plusSeconds(60));

        when(referralRequest2.getAuthoredOn()).thenReturn(after);
        when(referralRequest2.getResourceType()).thenReturn(ResourceType.ReferralRequest);
    }

    @Test
    public void shouldReturnNegativeOneForValidDateAndNull() {
        assertThat(resourceComparator.compare(null, referralRequest1)).isEqualTo(-1);
    }

    @Test
    public void shouldReturnOneForValidDates() {
        mockReferralRequest1();
        mockReferralRequest2();

        assertThat(resourceComparator.compare(referralRequest2, referralRequest1)).isEqualTo(1);
    }

    @Test
    public void shouldReturnOneForValidDateAndNull() {
        assertThat(resourceComparator.compare(referralRequest1, null)).isEqualTo(1);
    }

    @Test
    public void shouldReturnZeroForEqualDate() {
        mockReferralRequest1();

        assertThat(resourceComparator.compare(referralRequest1, referralRequest1)).isEqualTo(0);
    }

    @Test
    public void shouldReturnZeroForNull() {
        assertThat(resourceComparator.compare(null, null)).isEqualTo(0);
    }
}
