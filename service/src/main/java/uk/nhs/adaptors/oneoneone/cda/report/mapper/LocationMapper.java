package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01HealthCareFacility;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Location;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Place;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;

@Component
@AllArgsConstructor
public class LocationMapper {

    private final AddressMapper addressMapper;

    private final HumanNameMapper humanNameMapper;

    private final OrganizationMapper organizationMapper;

    private final ContactPointMapper contactPointMapper;

    private final NodeUtil nodeUtil;

    private final PeriodMapper periodMapper;

    private final ResourceUtil resourceUtil;

    public Location mapRoleToLocation(POCDMT000002UK01ParticipantRole role) {
        Location location = new Location();
        location.setIdElement(resourceUtil.newRandomUuid());
        if (role.sizeOfAddrArray() > 0) {
            location.setAddress(addressMapper.mapAddress(role.getAddrArray(0)));
        }

        if (role.isSetPlayingEntity()) {
            POCDMT000002UK01PlayingEntity playingEntity = role.getPlayingEntity();
            location.setName(humanNameMapper.mapHumanName(playingEntity.getNameArray(0)).getText());
            location.setDescription(nodeUtil.getNodeValueString(playingEntity.getDesc()));
        }
        return location;
    }

    public Encounter.EncounterLocationComponent mapOrganizationToLocationComponent(POCDMT000002UK01Organization organization) {
        Encounter.EncounterLocationComponent encounterLocationComponent = new Encounter.EncounterLocationComponent();
        encounterLocationComponent.setStatus(Encounter.EncounterLocationStatus.ACTIVE);

        Location location = new Location();
        location.setIdElement(resourceUtil.newRandomUuid());
        Organization managingOrganization = organizationMapper.mapOrganization(organization);
        location.setManagingOrganization(resourceUtil.createReference(managingOrganization));
        location.setManagingOrganizationTarget(managingOrganization);

        encounterLocationComponent.setLocation(resourceUtil.createReference(location));
        encounterLocationComponent.setLocationTarget(location);

        if (organization.isSetAsOrganizationPartOf()) {
            if (organization.getAsOrganizationPartOf().getEffectiveTime() != null) {
                encounterLocationComponent.setPeriod(
                    periodMapper.mapPeriod(organization.getAsOrganizationPartOf().getEffectiveTime()));
            }
        }

        return encounterLocationComponent;
    }

    public Location mapRecipientToLocation(POCDMT000002UK01IntendedRecipient intendedRecipient) {
        Location location = new Location();
        if (intendedRecipient.sizeOfAddrArray() > 0) {
            location.setAddress(addressMapper.mapAddress(intendedRecipient.getAddrArray(0)));
        }

        location.setTelecom(Arrays
            .stream(intendedRecipient.getTelecomArray())
            .map(contactPointMapper::mapContactPoint)
            .collect(Collectors.toList()));

        if (intendedRecipient.isSetReceivedOrganization()) {
            Organization managingOrganization = organizationMapper.mapOrganization(intendedRecipient.getReceivedOrganization());
            location.setManagingOrganization(resourceUtil.createReference(managingOrganization));
            location.setManagingOrganizationTarget(managingOrganization);
        }

        if (location.isEmpty()) {
            return null;
        }

        location.setIdElement(resourceUtil.newRandomUuid());
        return location;
    }

    public Encounter.EncounterLocationComponent mapHealthcareFacilityToLocationComponent(
        POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        POCDMT000002UK01EncompassingEncounter encompassingEncounter = clinicalDocument.getComponentOf().getEncompassingEncounter();
        POCDMT000002UK01Place place = Optional.ofNullable(encompassingEncounter)
            .map(POCDMT000002UK01EncompassingEncounter::getLocation)
            .map(POCDMT000002UK01Location::getHealthCareFacility)
            .map(POCDMT000002UK01HealthCareFacility::getLocation)
            .orElse(null);

        if (place != null) {
            Location location = new Location();
            location.setIdElement(resourceUtil.newRandomUuid());
            location.setName(nodeUtil.getAllText(place.getName().getDomNode()));

            AD address = place.getAddr();
            if (address != null) {
                location.setAddress(addressMapper.mapAddress(address));
            }

            Encounter.EncounterLocationComponent encounterLocationComponent = new Encounter.EncounterLocationComponent();
            encounterLocationComponent.setStatus(Encounter.EncounterLocationStatus.COMPLETED);
            encounterLocationComponent.setLocation(resourceUtil.createReference(location));
            encounterLocationComponent.setLocationTarget(location);
            return encounterLocationComponent;
        }
        return null;
    }
}
