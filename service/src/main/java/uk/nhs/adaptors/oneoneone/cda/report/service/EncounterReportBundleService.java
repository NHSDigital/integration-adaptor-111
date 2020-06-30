package uk.nhs.adaptors.oneoneone.cda.report.service;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CompositionMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ListMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.ArrayList;
import java.util.List;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

@Component
@AllArgsConstructor
public class EncounterReportBundleService {

    private EncounterMapper encounterMapper;
    private CompositionMapper compositionMapper;
    private ListMapper listMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        List<DomainResource> resourcesCreated = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);
        Composition composition = compositionMapper.mapComposition(clinicalDocument, encounter);

        addEncounter(resourcesCreated, bundle, encounter);
        addServiceProvider(resourcesCreated, bundle, encounter);
        addParticipants(resourcesCreated, bundle, encounter);
        addLocation(resourcesCreated, bundle, encounter);
        addSubject(resourcesCreated, bundle, encounter);
        addIncomingReferral(resourcesCreated, bundle, encounter);
        addAppointment(resourcesCreated, bundle, encounter);
        addEpisodeOfCare(resourcesCreated, bundle, encounter);
        addComposition(resourcesCreated, bundle, composition);

        ListResource listResource = listMapper.mapList(clinicalDocument, encounter, resourcesCreated);

        addList(bundle, listResource);

        return bundle;
    }

    private void addEncounter(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        addEntry(resourcesCreated, bundle, encounter);
    }

    private void addEpisodeOfCare(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        if (encounter.hasEpisodeOfCare()) {
            EpisodeOfCare episodeOfCare = (EpisodeOfCare) encounter.getEpisodeOfCareFirstRep().getResource();
            addEntry(resourcesCreated, bundle, episodeOfCare);

            if (episodeOfCare.hasCareManager()) {
                addEntry(resourcesCreated, bundle, episodeOfCare.getCareManagerTarget());
            }

            if (episodeOfCare.hasManagingOrganization()) {
                addEntry(resourcesCreated, bundle, episodeOfCare.getManagingOrganizationTarget());
            }
        }
    }

    private void addServiceProvider(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        addEntry(resourcesCreated, bundle, encounter.getServiceProviderTarget());
    }

    private void addParticipants(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        List<Encounter.EncounterParticipantComponent> participantComponents = encounter.getParticipant();
        participantComponents.stream().forEach(item -> addEntry(resourcesCreated, bundle, item.getIndividualTarget()));
    }

    private void addAppointment(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        if (encounter.hasAppointment()) {
            Appointment appointment = encounter.getAppointmentTarget();
            addEntry(resourcesCreated, bundle, appointment);
            if (appointment.hasParticipant()) {
                for (Appointment.AppointmentParticipantComponent participant : appointment.getParticipant()) {
                    if (participant.hasActor()) {
                        addEntry(resourcesCreated, bundle, participant.getActorTarget());
                    }
                }
            }
        }
    }

    private void addLocation(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        List<Encounter.EncounterLocationComponent> locationComponents = encounter.getLocation();
        for (Encounter.EncounterLocationComponent component : locationComponents) {
            if (component.hasLocation()) {
                Location location = component.getLocationTarget();
                addEntry(resourcesCreated, bundle, location);
                if (location.hasManagingOrganization()) {
                    addOrganization(resourcesCreated, bundle, location.getManagingOrganizationTarget());
                }
            }
        }
    }

    private void addOrganization(List<DomainResource> resourcesCreated, Bundle bundle, Organization organization) {
        addEntry(resourcesCreated, bundle, organization);
        if (organization.hasPartOf()) {
            addOrganization(resourcesCreated, bundle, organization.getPartOfTarget());
        }
    }

    private void addSubject(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        if (encounter.getSubjectTarget() instanceof Patient) {
            Patient patient = (Patient) encounter.getSubjectTarget();
            addEntry(resourcesCreated, bundle, patient);

            if (patient.hasGeneralPractitioner()) {
                for (Reference gp : patient.getGeneralPractitioner()){
                    Organization organization = (Organization) gp.getResource();
                    addEntry(resourcesCreated, bundle, organization);
                }
            }
        }
        if (encounter.getSubjectTarget() instanceof Group) {
            Group group = (Group) encounter.getSubjectTarget();
            addEntry(resourcesCreated, bundle, group);
            for (Group.GroupMemberComponent groupMemberComponent : group.getMember()) {
                bundle.addEntry()
                        .setFullUrl(groupMemberComponent.getIdElement().getValue())
                        .setResource(groupMemberComponent.getEntityTarget());
            }
        }
    }

    private void addIncomingReferral(List<DomainResource> resourcesCreated, Bundle bundle, Encounter encounter) {
        ReferralRequest referralRequest = (ReferralRequest) encounter.getIncomingReferralFirstRep().getResource();
        addEntry(resourcesCreated, bundle, referralRequest);
        if (referralRequest.hasSubject()) {
            addEntry(resourcesCreated, bundle, referralRequest.getSubjectTarget());
        }
        if (referralRequest.hasRecipient()) {
            for (Reference recipient :
                    referralRequest.getRecipient()) {
                addEntry(resourcesCreated, bundle, (Resource) recipient.getResource());
                HealthcareService healthcareService = (HealthcareService) recipient.getResource();
                if (healthcareService.hasLocation()){
                    addEntry(resourcesCreated, bundle, (Location) healthcareService.getLocationFirstRep().getResource());
                }
                if (healthcareService.hasProvidedBy()){
                    addEntry(resourcesCreated, bundle, healthcareService.getProvidedByTarget());
                }
            }
        }
        if (referralRequest.hasRequester()) {
            addEntry(resourcesCreated, bundle, referralRequest.getRequester().getOnBehalfOfTarget());
        }
    }

    private void addComposition(List<DomainResource> resourcesCreated, Bundle bundle, Composition composition) {
        addEntry(resourcesCreated, bundle, composition);
        if (composition.hasAuthor()) {
            addEntry(resourcesCreated, bundle, (Resource) composition.getAuthorFirstRep().getResource());
        }
    }

    private void addList(Bundle bundle, ListResource listResource) {
        bundle.addEntry()
                .setFullUrl(listResource.getIdElement().getValue())
                .setResource(listResource);
    }

    private static void addEntry(List<DomainResource> resourcesCreated, Bundle bundle, Resource resource) {
        bundle.addEntry()
                .setFullUrl(resource.getIdElement().getValue())
                .setResource(resource);
        resourcesCreated.add((DomainResource) resource);
    }
}
