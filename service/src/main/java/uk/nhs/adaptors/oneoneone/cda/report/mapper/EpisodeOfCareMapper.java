package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.springframework.stereotype.Component;

@Component
public class EpisodeOfCareMapper {
    public EpisodeOfCare mapEpisodeOfCare() {
        EpisodeOfCare episodeOfCare = new EpisodeOfCare();
        episodeOfCare.setIdElement(newRandomUuid());

        return episodeOfCare;
    }
}
