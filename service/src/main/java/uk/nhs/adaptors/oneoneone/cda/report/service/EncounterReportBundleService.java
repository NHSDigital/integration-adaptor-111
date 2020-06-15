package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.hl7.fhir.dstu3.model.Bundle.BundleType.TRANSACTION;

import java.util.List;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@AllArgsConstructor
public class EncounterReportBundleService {

    private EncounterMapper encounterMapper;

    public Bundle createEncounterBundle(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        Bundle bundle = new Bundle();
        bundle.setType(TRANSACTION);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);

        addEncounter(bundle, encounter);
        addServiceProvider(bundle, encounter);
        addParticipants(bundle, encounter);
        addAppointment(bundle, encounter);
        addLocation(bundle, encounter);

        return bundle;
    }

    private void addEncounter(Bundle bundle, Encounter encounter) {
        bundle.addEntry()
            .setFullUrl(encounter.getIdElement().getValue())
            .setResource(encounter);
    }

    private void addServiceProvider(Bundle bundle, Encounter encounter) {
        Organization organization = encounter.getServiceProviderTarget();
        bundle.addEntry()
            .setFullUrl(organization.getIdElement().getValue())
            .setResource(organization);
    }

    private void addParticipants(Bundle bundle, Encounter encounter) {
        List<Encounter.EncounterParticipantComponent> participantComponents = encounter.getParticipant();
        for (Encounter.EncounterParticipantComponent participantComponent : participantComponents) {
            bundle.addEntry()
                .setFullUrl(participantComponent.getIndividualTarget().getIdElement().getValue())
                .setResource(participantComponent.getIndividualTarget());
        }
    }

    private void addAppointment(Bundle bundle, Encounter encounter) {
        if (encounter.hasAppointment()) {
            Appointment appointment = encounter.getAppointmentTarget();
            bundle.addEntry()
                .setFullUrl(appointment.getIdElement().getValue())
                .setResource(appointment);
            if (appointment.hasParticipant()) {
                for (Appointment.AppointmentParticipantComponent participant : appointment.getParticipant()) {
                    if (participant.hasActor()) {
                        bundle.addEntry()
                            .setFullUrl(participant.getActorTarget().getIdElement().getValue())
                            .setResource(participant.getActorTarget());
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
                bundle.addEntry()
                    .setFullUrl(component.getLocationTarget().getIdElement().getValue())
                    .setResource(component.getLocationTarget());
                if (location.hasManagingOrganization()) {
                    addOrganization(bundle, location.getManagingOrganizationTarget());
                }
            }
        }
    }

    private void addOrganization(Bundle bundle, Organization organization) {
        bundle.addEntry()
            .setFullUrl(organization.getIdElement().getValue())
            .setResource(organization);
        if (organization.hasPartOf()) {
            addOrganization(bundle, organization.getPartOfTarget());
        }
    }
}
