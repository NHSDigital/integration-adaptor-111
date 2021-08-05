package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01OrganizationPartOf;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtilTest.verifyUUID;

@ExtendWith(MockitoExtension.class)
public class LocationMapperTest {

    public static final String DESCRIPTION = "description";
    public static final String NAME = "Mick Jones";
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private HumanNameMapper humanNameMapper;
    @Mock
    private OrganizationMapper organizationMapper;
    @Mock
    private PeriodMapper periodMapper;
    @Mock
    private ContactPointMapper contactPointMapper;
    @InjectMocks
    private LocationMapper locationMapper;
    @Mock
    private Address address;
    @Mock
    private HumanName humanName;
    @Mock
    private Organization organization;
    @Mock
    private Period period;
    @Mock
    private ContactPoint contactPoint;
    @Mock
    private NodeUtil nodeUtil;

    @Test
    public void shouldMapRoleToLocation() {
        POCDMT000002UK01ParticipantRole participantRole = mock(POCDMT000002UK01ParticipantRole.class);
        POCDMT000002UK01PlayingEntity playingEntity = mock(POCDMT000002UK01PlayingEntity.class);
        AD itkAddress = mock(AD.class);
        PN personName = mock(PN.class);

        when(participantRole.sizeOfAddrArray()).thenReturn(1);
        when(participantRole.isSetPlayingEntity()).thenReturn(true);
        when(participantRole.getAddrArray(anyInt())).thenReturn(itkAddress);
        when(participantRole.getPlayingEntity()).thenReturn(playingEntity);

        when(playingEntity.getNameArray(anyInt())).thenReturn(personName);
        when(humanNameMapper.mapHumanName(isA(PN.class))).thenReturn(humanName);
        when(addressMapper.mapAddress(isA(AD.class))).thenReturn(address);
        when(humanName.getText()).thenReturn(NAME);
        when(nodeUtil.getNodeValueString(playingEntity.getDesc())).thenReturn(DESCRIPTION);

        Location location = locationMapper.mapRoleToLocation(participantRole);

        assertThat(location.getAddress()).isEqualTo(address);
        assertThat(location.getName()).isEqualTo(NAME);
        assertThat(location.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void shouldMapOrganizationToLocationComponent() {
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        POCDMT000002UK01OrganizationPartOf partOf = mock(POCDMT000002UK01OrganizationPartOf.class);
        IVLTS effectiveTime = mock(IVLTS.class);

        when(organizationMapper.mapOrganization(any())).thenReturn(organization);
        when(itkOrganization.isSetAsOrganizationPartOf()).thenReturn(true);
        when(itkOrganization.getAsOrganizationPartOf()).thenReturn(partOf);
        when(partOf.getEffectiveTime()).thenReturn(effectiveTime);
        when(periodMapper.mapPeriod(any())).thenReturn(period);

        Encounter.EncounterLocationComponent encounterLocationComponent = locationMapper
                .mapOrganizationToLocationComponent(itkOrganization);

        assertThat(verifyUUID(encounterLocationComponent.getLocationTarget().getIdElement().getValue())).isEqualTo(true);
        assertThat(encounterLocationComponent.getLocationTarget().getManagingOrganizationTarget()).isEqualTo(organization);
        assertThat(encounterLocationComponent.getPeriod()).isEqualTo(period);
    }

    @Test
    public void shouldMapRecipientToLocation() {
        POCDMT000002UK01IntendedRecipient itkIntendedRecipient = mock(POCDMT000002UK01IntendedRecipient.class);

        AD itkAddress = mock(AD.class);
        TEL itkTelecom = mock(TEL.class);

        when(itkIntendedRecipient.sizeOfAddrArray()).thenReturn(new AD[]{itkAddress}.length);
        when(addressMapper.mapAddress(any())).thenReturn(address);
        when(itkIntendedRecipient.getTelecomArray()).thenReturn(new TEL[]{itkTelecom});
        when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);
        when(itkIntendedRecipient.isSetReceivedOrganization()).thenReturn(true);
        when(organizationMapper.mapOrganization(any())).thenReturn(organization);

        Location referenceRecipientToLocation = locationMapper
                .mapRecipientToLocation(itkIntendedRecipient);

        assertThat(referenceRecipientToLocation.getId().startsWith("urn:uuid:"));
        assertThat(referenceRecipientToLocation.getAddress()).isEqualTo(address);
        assertThat(referenceRecipientToLocation.getTelecom()).isEqualTo(List.of(contactPoint));
        assertThat(referenceRecipientToLocation.getManagingOrganization()).isNotNull();
        assertThat(referenceRecipientToLocation.getManagingOrganizationTarget()).isEqualTo(organization);
    }
}
