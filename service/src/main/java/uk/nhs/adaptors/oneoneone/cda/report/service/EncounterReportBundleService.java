package uk.nhs.adaptors.oneoneone.cda.report.service;

import static java.util.stream.Collectors.toSet;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

import java.util.Collection;
import java.util.List;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Consent;
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

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CarePlanMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CompositionMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ConsentMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.HealthcareServiceMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ListMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@AllArgsConstructor
public class EncounterReportBundleService {

    private final EncounterMapper encounterMapper;
    private final CompositionMapper compositionMapper;
    private final ListMapper listMapper;
    private final CarePlanMapper carePlanMapper;
    private final ConsentMapper consentMapper;
    private final HealthcareServiceMapper healthcareServiceMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        List<HealthcareService> healthcareServiceList = healthcareServiceMapper.mapHealthcareService(clinicalDocument);
        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, healthcareServiceList);
        Composition composition = compositionMapper.mapComposition(clinicalDocument, encounter);
        List<CarePlan> carePlans = carePlanMapper.mapCarePlan(clinicalDocument, encounter);
        Consent consent = consentMapper.mapConsent(clinicalDocument, encounter);

        addEncounter(bundle, encounter);
        addServiceProvider(bundle, encounter);
        addParticipants(bundle, encounter);
        addLocation(bundle, encounter);
        addSubject(bundle, encounter);
        addHealthcareService(bundle, healthcareServiceList);
        addIncomingReferral(bundle, encounter);
        addAppointment(bundle, encounter);
        addEpisodeOfCare(bundle, encounter);
        addComposition(bundle, composition);
        addCarePlan(bundle, carePlans);
        addConsent(bundle, consent);
        addCondition(bundle, encounter);

        ListResource listResource = getReferenceFromBundle(bundle, clinicalDocument, encounter);
        addList(bundle, listResource);

        return bundle;
    }

    private ListResource getReferenceFromBundle(Bundle bundle, POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        Collection<Resource> resourcesCreated = bundle.getEntry().stream().map(it -> it.getResource()).collect(toSet());
        return listMapper.mapList(clinicalDocument, encounter, resourcesCreated);
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
        participantComponents.forEach(item -> addEntry(bundle, item.getIndividualTarget()));
    }

    private void addAppointment(Bundle bundle, Encounter encounter) {
        if (encounter.hasAppointment()) {
            Appointment appointment = encounter.getAppointmentTarget();
            addEntry(bundle, appointment);
            if (appointment.hasParticipant()) {
                for (Appointment.AppointmentParticipantComponent participant : appointment.getParticipant()) {
                    if (participant.hasActor()) {
                        Resource actorTarget = participant.getActorTarget();
                        if (!(actorTarget instanceof Patient)) {
                            addEntry(bundle, actorTarget);
                        }
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
                for (Reference gp : patient.getGeneralPractitioner()) {
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

        if (referralRequest.hasRequester()) {
            addEntry(bundle, referralRequest.getRequester().getOnBehalfOfTarget());
        }
    }

    private void addCondition(Bundle bundle, Encounter encounter) {
        if (encounter.hasDiagnosis()) {
            Condition condition = (Condition) encounter.getDiagnosisFirstRep().getConditionTarget();
            if (condition != null) {
                addEntry(bundle, condition);
            }
        }
    }

    private void addComposition(Bundle bundle, Composition composition) {
        addEntry(bundle, composition);
        if (composition.hasAuthor()) {
            addEntry(bundle, (Resource) composition.getAuthorFirstRep().getResource());
        }
    }

    private void addList(Bundle bundle, ListResource listResource) {
        addEntry(bundle, listResource);
    }

    private void addCarePlan(Bundle bundle, List<CarePlan> carePlans) {
        carePlans.stream().forEach(carePlan -> addEntry(bundle, carePlan));
    }

    private void addHealthcareService(Bundle bundle, List<HealthcareService> healthcareServiceList) {
        for (HealthcareService healthcareService : healthcareServiceList) {
            addEntry(bundle, healthcareService);
            if (healthcareService.hasLocation()) {
                addEntry(bundle, (Location) healthcareService.getLocationFirstRep().getResource());
            }
            if (healthcareService.hasProvidedBy()) {
                addEntry(bundle, healthcareService.getProvidedByTarget());
            }
        }
    }

    private void addConsent(Bundle bundle, Consent consent) {
        addEntry(bundle, consent);
    }

    private static void addEntry(Bundle bundle, Resource resource) {
        bundle.addEntry()
            .setFullUrl(resource.getIdElement().getValue())
            .setResource(resource);
    }
}
