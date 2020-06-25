package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class LocationMapper {

    private AddressMapper addressMapper;

    private HumanNameMapper humanNameMapper;

    private OrganizationMapper organizationMapper;

    public Location mapRoleToLocation(POCDMT000002UK01ParticipantRole role) {
        Location location = new Location();
        location.setIdElement(IdType.newRandomUuid());
        if (role.sizeOfAddrArray() > 0) {
            location.setAddress(addressMapper.mapAddress(role.getAddrArray(0)));
        }

        if (role.isSetPlayingEntity()) {
            POCDMT000002UK01PlayingEntity playingEntity = role.getPlayingEntity();
            location.setName(humanNameMapper.mapHumanName(playingEntity.getNameArray(0)).getText());
            location.setDescription(NodeUtil.getNodeValueString(playingEntity.getDesc()));
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

        return encounterLocationComponent;
    }

    public Location mapRecipientToLocation(POCDMT000002UK01IntendedRecipient intendedRecipient) {
        Location location = new Location();
        location.setIdElement(IdType.newRandomUuid());
        if (intendedRecipient.sizeOfAddrArray() > 0) {
            location.setAddress(addressMapper.mapAddress(intendedRecipient.getAddrArray(0)));
        }
        return location;
    }

}
