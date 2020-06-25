package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Encounter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.ED;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationMapperTest {

    public static final String DESCRIPTION = "description";
    public static final String NAME = "Mick Jones";
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private HumanNameMapper humanNameMapper;
    @Mock
    private OrganizationMapper organizationMapper;
    @InjectMocks
    private LocationMapper locationMapper;
    @Mock
    private Address address;
    @Mock
    private HumanName humanName;
    @Mock
    private Organization organization;

    @Test
    public void mapRoleToLocation() {
        POCDMT000002UK01ParticipantRole participantRole = mock(POCDMT000002UK01ParticipantRole.class);
        POCDMT000002UK01PlayingEntity playingEntity = mock(POCDMT000002UK01PlayingEntity.class);
        AD itkAddress = mock(AD.class);
        PN personName = mock(PN.class);
        ED entityDescription = mockEntityDescription();

        when(participantRole.sizeOfAddrArray()).thenReturn(1);
        when(participantRole.isSetPlayingEntity()).thenReturn(true);
        when(participantRole.getAddrArray(anyInt())).thenReturn(itkAddress);
        when(participantRole.getPlayingEntity()).thenReturn(playingEntity);

        when(playingEntity.getNameArray(anyInt())).thenReturn(personName);
        when(playingEntity.getDesc()).thenReturn(entityDescription);
        when(humanNameMapper.mapHumanName(isA(PN.class))).thenReturn(humanName);
        when(addressMapper.mapAddress(isA(AD.class))).thenReturn(address);
        when(humanName.getText()).thenReturn(NAME);

        Location location = locationMapper.mapRoleToLocation(participantRole);

        assertThat(location.getAddress()).isEqualTo(address);
        assertThat(location.getName()).isEqualTo(NAME);
        assertThat(location.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void mapOrganizationToLocationComponent() {
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);

        when(organizationMapper.mapOrganization(any())).thenReturn(organization);

        Encounter.EncounterLocationComponent encounterLocationComponent = locationMapper
                .mapOrganizationToLocationComponent(itkOrganization);

        assertThat(encounterLocationComponent.getLocationTarget().getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounterLocationComponent.getLocationTarget().getManagingOrganizationTarget()).isEqualTo(organization);
    }

    @Test
    public void mapRecipientToLocation() {
        POCDMT000002UK01IntendedRecipient itkIntendedRecipient = mock(POCDMT000002UK01IntendedRecipient.class);

        Location referenceRecipientToLocation = locationMapper
                .mapRecipientToLocation(itkIntendedRecipient);

        assertThat(referenceRecipientToLocation.getId().startsWith("urn:uuid:"));
    }

    private ED mockEntityDescription() {
        ED entityDescription = mock(ED.class);
        Node edNode = mock(Node.class);
        Node edSubnode = mock(Node.class);
        when(entityDescription.getDomNode()).thenReturn(edNode);
        when(edNode.getFirstChild()).thenReturn(edSubnode);
        when(edSubnode.getNodeValue()).thenReturn(DESCRIPTION);
        return entityDescription;
    }
}
