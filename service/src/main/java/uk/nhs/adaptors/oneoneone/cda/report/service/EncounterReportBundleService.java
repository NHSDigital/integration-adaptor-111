package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

import java.util.List;

import org.hl7.fhir.dstu3.model.*;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CarePlanMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@AllArgsConstructor
public class EncounterReportBundleService {

    private EncounterMapper encounterMapper;
    private CarePlanMapper carePlanMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);
        List<CarePlan> carePlans = carePlanMapper.mapCarePlan(clinicalDocument, encounter);

        addEncounter(bundle, encounter);
        addServiceProvider(bundle, encounter);
        addParticipants(bundle, encounter);
        addLocation(bundle, encounter);
        addSubject(bundle, encounter);
        addIncomingReferral(bundle, encounter);
        addAppointment(bundle, encounter);
        addEpisodeOfCare(bundle, encounter);
        addCarePlan(bundle, carePlans);
        return bundle;
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

    private void addSubject(Bundle bundle, Encounter encounter) {
        if (encounter.getSubjectTarget() instanceof Patient) {
            Patient patient = (Patient) encounter.getSubjectTarget();
            addEntry(bundle, patient);

            if (patient.hasGeneralPractitioner()) {
                for (Reference gp : patient.getGeneralPractitioner()){
                    Organization organization = (Organization) gp.getResource();
                    addEntry(bundle, organization);
                }
            }
        }
        if (encounter.getSubjectTarget() instanceof Group) {
            Group group = (Group) encounter.getSubjectTarget();
            addEntry(bundle, group);
            for (Group.GroupMemberComponent groupMemberComponent : group.getMember()) {
                bundle.addEntry()
                        .setFullUrl(groupMemberComponent.getIdElement().getValue())
                        .setResource(groupMemberComponent.getEntityTarget());
            }
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

    private void addCarePlan(Bundle bundle, List<CarePlan> carePlans) {
        if (!carePlans.isEmpty())
            carePlans.stream().forEach(carePlan -> addEntry(bundle, carePlan));
    }

    private static void addEntry(Bundle bundle, Resource resource) {
        bundle.addEntry()
                .setFullUrl(resource.getIdElement().getValue())
                .setResource(resource);
    }
}
