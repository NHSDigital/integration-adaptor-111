package uk.nhs.adaptors.oneoneone.cda.report.service;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.List;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

@Component
@AllArgsConstructor
public class EncounterReportBundleService {

    private EncounterMapper encounterMapper;

    private static void addEntry(Bundle bundle, Resource resource) {
        bundle.addEntry()
                .setFullUrl(resource.getIdElement().getValue())
                .setResource(resource);
    }

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);

        addEncounter(bundle, encounter);
        addServiceProvider(bundle, encounter);
        addParticipants(bundle, encounter);
        addAppointment(bundle, encounter);
        addLocation(bundle, encounter);
        addIncomingReferral(bundle, encounter);
        addPatient(bundle, encounter);
        addEpisodeOfCare(bundle, encounter);

        return bundle;
    }

    private void addPatient(Bundle bundle, Encounter encounter) {
        Patient patient = (Patient) encounter.getSubject().getResource();
        addEntry(bundle, encounter.getSubjectTarget());
        if (patient.hasGeneralPractitioner()) {
            addEntry(bundle, (Organization) patient.getGeneralPractitionerFirstRep().getResource());
        }
    }

    private void addEncounter(Bundle bundle, Encounter encounter) {
        addEntry(bundle, encounter);
    }

    private void addEpisodeOfCare(Bundle bundle, Encounter encounter) {
        if (encounter.hasEpisodeOfCare()) {
            EpisodeOfCare episodeOfCare = (EpisodeOfCare) encounter.getEpisodeOfCareFirstRep().getResource();
            addEntry(bundle, episodeOfCare);

            if (episodeOfCare.hasCareManager()) {
                addEntry(bundle, episodeOfCare.getCareManagerTarget());
            }

            if (episodeOfCare.hasManagingOrganization()) {
                addEntry(bundle, episodeOfCare.getManagingOrganizationTarget());
            }
        }
    }

    private void addServiceProvider(Bundle bundle, Encounter encounter) {
        addEntry(bundle, encounter.getServiceProviderTarget());
    }

    private void addParticipants(Bundle bundle, Encounter encounter) {
        List<Encounter.EncounterParticipantComponent> participantComponents = encounter.getParticipant();
        participantComponents.stream().forEach(item -> addEntry(bundle, item.getIndividualTarget()));
    }

    private void addAppointment(Bundle bundle, Encounter encounter) {
        if (encounter.hasAppointment()) {
            Appointment appointment = encounter.getAppointmentTarget();
            addEntry(bundle, appointment);
            if (appointment.hasParticipant()) {
                for (Appointment.AppointmentParticipantComponent participant : appointment.getParticipant()) {
                    if (participant.hasActor()) {
                        addEntry(bundle, participant.getActorTarget());
                    }
                }
            }
        }
    }

    private void addLocation(Bundle bundle, Encounter encounter) {
        List<Encounter.EncounterLocationComponent> locationComponents = encounter.getLocation();
        for (Encounter.EncounterLocationComponent component : locationComponents) {
            if (component.hasLocation()) {
                Location location = component.getLocationTarget();
                addEntry(bundle, location);
                if (location.hasManagingOrganization()) {
                    addOrganization(bundle, location.getManagingOrganizationTarget());
                }
            }
        }
    }

    private void addOrganization(Bundle bundle, Organization organization) {
        addEntry(bundle, organization);
        if (organization.hasPartOf()) {
            addOrganization(bundle, organization.getPartOfTarget());
        }
    }

    private void addIncomingReferral(Bundle bundle, Encounter encounter) {
        ReferralRequest referralRequest = (ReferralRequest) encounter.getIncomingReferralFirstRep().getResource();
        addEntry(bundle, referralRequest);
        if (referralRequest.hasSubject()) {
            addEntry(bundle, referralRequest.getSubjectTarget());
        }
        if (referralRequest.hasRecipient()) {
            for (Reference recipient :
                    referralRequest.getRecipient()) {
                addEntry(bundle, (Resource) recipient.getResource());
                HealthcareService healthcareService = (HealthcareService) recipient.getResource();
                if (healthcareService.hasLocation()){
                    addEntry(bundle, (Location) healthcareService.getLocationFirstRep().getResource());
                }
                if (healthcareService.hasProvidedBy()){
                    addEntry(bundle, healthcareService.getProvidedByTarget());
                }
            }
        }
        if (referralRequest.hasRequester()) {
            addEntry(bundle, referralRequest.getRequester().getOnBehalfOfTarget());
        }

    }
}
