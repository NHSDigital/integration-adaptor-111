package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
public class EncounterBundleService {

    @Autowired
    private EncounterMapper encounterMapper;

    @Autowired
    private EpisodeOfCareMapper episodeOfCareMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocumentDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter();

        addEncounter(bundle, encounter);
        addEpisodeOfCare(bundle, encounter);

        return bundle;
    }

    private void addEncounter(Bundle bundle, Encounter encounter) {
        bundle.addEntry()
            .setFullUrl(encounter.getIdElement().getValue())
            .setResource(encounter);
    }

    private void addEpisodeOfCare(Bundle bundle, Encounter encounter) {
        EpisodeOfCare episodeOfCare = episodeOfCareMapper.mapEpisodeOfCare();

        bundle.addEntry()
            .setFullUrl(episodeOfCare.getIdElement().getValue())
            .setResource(episodeOfCare);
        encounter.addEpisodeOfCare(new Reference(episodeOfCare));
    }
}
