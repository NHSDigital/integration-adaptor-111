package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.PN;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01OrganizationPartOf;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PlayingEntity;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@ExtendWith(MockitoExtension.class)
public class LocationMapperTest {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "Mick Jones";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
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
    @Mock
    private ResourceUtil resourceUtil;

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
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        Location location = locationMapper.mapRoleToLocation(participantRole);

        assertThat(location.getAddress()).isEqualTo(address);
        assertThat(location.getName()).isEqualTo(NAME);
        assertThat(location.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(location.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }

    @Test
    public void shouldMapOrganizationToLocationComponent() {
        POCDMT000002UK01Organization itkOrganization = mock(POCDMT000002UK01Organization.class);
        POCDMT000002UK01OrganizationPartOf partOf = mock(POCDMT000002UK01OrganizationPartOf.class);
        IVLTS effectiveTime = mock(IVLTS.class);

        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(organization);
        when(itkOrganization.isSetAsOrganizationPartOf()).thenReturn(true);
        when(itkOrganization.getAsOrganizationPartOf()).thenReturn(partOf);
        when(partOf.getEffectiveTime()).thenReturn(effectiveTime);
        when(periodMapper.mapPeriod(any())).thenReturn(period);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        Encounter.EncounterLocationComponent encounterLocationComponent = locationMapper
            .mapOrganizationToLocationComponent(itkOrganization);

        assertThat(encounterLocationComponent.getLocationTarget().getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(encounterLocationComponent.getLocationTarget().getManagingOrganizationTarget()).isEqualTo(organization);
        assertThat(encounterLocationComponent.getPeriod()).isEqualTo(period);
    }

    @Test
    public void shouldMapRecipientToLocation() {
        POCDMT000002UK01IntendedRecipient itkIntendedRecipient = prepareIntendedRecipientMocks();

        Location referenceRecipientToLocation = locationMapper
            .mapRecipientToLocation(itkIntendedRecipient, organization);

        assertThat(referenceRecipientToLocation.getId()).isEqualTo(RANDOM_UUID);
        assertThat(referenceRecipientToLocation.getAddress()).isEqualTo(address);
        assertThat(referenceRecipientToLocation.getTelecom()).isEqualTo(List.of(contactPoint));
        assertThat(referenceRecipientToLocation.getManagingOrganization()).isNotNull();
        assertThat(referenceRecipientToLocation.getManagingOrganizationTarget()).isEqualTo(organization);
        assertThat(referenceRecipientToLocation.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }

    @Test
    public void shouldMapRecipientToLocationWithoutOrganizationIfItsEmpty() {
        POCDMT000002UK01IntendedRecipient itkIntendedRecipient = prepareIntendedRecipientMocks();

        Location referenceRecipientToLocation = locationMapper
            .mapRecipientToLocation(itkIntendedRecipient, new Organization());

        assertThat(referenceRecipientToLocation.getId()).isEqualTo(RANDOM_UUID);
        assertThat(referenceRecipientToLocation.getAddress()).isEqualTo(address);
        assertThat(referenceRecipientToLocation.getTelecom()).isEqualTo(List.of(contactPoint));
        assertThat(referenceRecipientToLocation.hasManagingOrganization()).isFalse();
        assertThat(referenceRecipientToLocation.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }

    @Test
    public void shouldMapHealthcareFacilityToLocation() {
        String locationName = "Moving castle";
        AD itkAddress = mock(AD.class);
        when(nodeUtil.getAllText(any())).thenReturn(locationName);
        when(clinicalDocument.getComponentOf().getEncompassingEncounter().getLocation().getHealthCareFacility().getLocation().getAddr())
            .thenReturn(itkAddress);
        when(addressMapper.mapAddress(eq(itkAddress))).thenReturn(address);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        Encounter.EncounterLocationComponent locationComponent = locationMapper.mapHealthcareFacilityToLocationComponent(clinicalDocument);
        assertThat(locationComponent.getLocationTarget().getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(locationComponent.getLocationTarget().getAddress()).isEqualTo(address);
        assertThat(locationComponent.getLocationTarget().getName()).isEqualTo(locationName);
        assertThat(locationComponent.getStatus()).isEqualTo(Encounter.EncounterLocationStatus.COMPLETED);
    }

    private POCDMT000002UK01IntendedRecipient prepareIntendedRecipientMocks() {
        POCDMT000002UK01IntendedRecipient itkIntendedRecipient = mock(POCDMT000002UK01IntendedRecipient.class);

        AD itkAddress = mock(AD.class);
        TEL itkTelecom = mock(TEL.class);

        when(itkIntendedRecipient.sizeOfAddrArray()).thenReturn(new AD[] {itkAddress}.length);
        when(addressMapper.mapAddress(any())).thenReturn(address);
        when(itkIntendedRecipient.getTelecomArray()).thenReturn(new TEL[] {itkTelecom});
        when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        return itkIntendedRecipient;
    }
}
