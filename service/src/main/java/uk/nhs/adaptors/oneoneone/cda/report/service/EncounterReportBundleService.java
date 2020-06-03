package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ServiceProviderMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
public class EncounterReportBundleService {

    @Autowired
    private EncounterMapper encounterMapper;

    @Autowired
    private ServiceProviderMapper serviceProviderMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocumentDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocumentDocument);

        addEncounter(bundle, encounter);
        addServiceProvider(bundle, encounter);

        return bundle;
    }

    private void addEncounter(Bundle bundle, Encounter encounter) {
        bundle.addEntry()
            .setFullUrl(encounter.getIdElement().getValue())
            .setResource(encounter);
    }

    private void addServiceProvider(Bundle bundle, Encounter encounter) {
        Organization organization = serviceProviderMapper.mapServiceProvider();

        bundle.addEntry()
            .setFullUrl(organization.getIdElement().getValue())
            .setResource(organization);
        encounter.setServiceProvider(new Reference(organization));

    }
}
