package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;

import org.hl7.fhir.dstu3.model.Location;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LocationMapper {

    private AddressMapper addressMapper;

    private HumanNameMapper humanNameMapper;

    public Location mapRoleToLocation(POCDMT000002UK01ParticipantRole role) {
        Location location = new Location();
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

}
