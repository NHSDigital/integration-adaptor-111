package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.junit.Test;

public class EpisodeOfCareMapperTest {

    private EpisodeOfCareMapper episodeOfCareMapper = new EpisodeOfCareMapper();

    @Test
    public void mapEpisodeOfCare() {
        EpisodeOfCare episodeOfCare = episodeOfCareMapper.mapEpisodeOfCare();

        assertThat(episodeOfCare.getIdElement().getValue()).startsWith("urn:uuid:");
    }
}
