package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil.newRandomUuid;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
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

    public Location mapRoleToLocation(POCDMT000002UK01ParticipantRole role) {
        Location location = new Location();
        location.setIdElement(newRandomUuid());
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
        location.setIdElement(newRandomUuid());
        Organization managingOrganization = organizationMapper.mapOrganization(organization);
        location.setManagingOrganization(new Reference(managingOrganization));
        location.setManagingOrganizationTarget(managingOrganization);

        encounterLocationComponent.setLocation(new Reference(location));
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
            location.setManagingOrganization(new Reference(managingOrganization));
            location.setManagingOrganizationTarget(managingOrganization);
        }

        if (location.isEmpty()) {
            return null;
        }

        location.setIdElement(newRandomUuid());
        return location;
    }
}
