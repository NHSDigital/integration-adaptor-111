package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static uk.nhs.connect.iucds.cda.ucr.XInformationRecipientX.Enum.forString;

import java.util.List;

import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

@ExtendWith(MockitoExtension.class)
public class HealthcareServiceMapperTest {

    private static final String HEALTHCARE_SERVICE_NAME = "Thames Medical Practice";
    private static final String PRCP_TYPE_CODE = "PRCP";
    private static final String TRC_TYPE_CODE = "TRC";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    @InjectMocks
    private HealthcareServiceMapper healthcareServiceMapper;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private OrganizationMapper organizationMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private POCDMT000002UK01IntendedRecipient intendedRecipient;
    @Mock
    private POCDMT000002UK01InformationRecipient informationRecipient;
    @Mock
    private POCDMT000002UK01Organization receivedOrganization;
    @Mock
    private ON name;
    @Mock
    private Node node;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private Organization organization;
    @Mock
    private Location location;
    @Mock
    private ResourceUtil resourceUtil;

    @BeforeEach
    public void setup() {
        POCDMT000002UK01InformationRecipient[] informationRecipientArray = new POCDMT000002UK01InformationRecipient[1];
        when(clinicalDocument.getInformationRecipientArray()).thenReturn(informationRecipientArray);
        informationRecipientArray[0] = informationRecipient;
    }

    @Test
    public void shouldMapHealthcareService() {
        when(informationRecipient.getIntendedRecipient()).thenReturn(intendedRecipient);
        when(intendedRecipient.isSetReceivedOrganization()).thenReturn(true);
        when(intendedRecipient.getReceivedOrganization()).thenReturn(receivedOrganization);
        when(receivedOrganization.sizeOfNameArray()).thenReturn(1);
        when(receivedOrganization.getNameArray(0)).thenReturn(name);
        when(organizationMapper.mapOrganization(informationRecipient)).thenReturn(organization);
        when(locationMapper.mapRecipientToLocation(intendedRecipient, organization)).thenReturn(location);
        when(name.getDomNode()).thenReturn(node);
        when(nodeUtil.getAllText(name.getDomNode())).thenReturn(HEALTHCARE_SERVICE_NAME);
        when(informationRecipient.getTypeCode()).thenReturn(forString(PRCP_TYPE_CODE));
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
        List<HealthcareService> healthcareServiceList = healthcareServiceMapper
            .mapHealthcareService(clinicalDocument);

        HealthcareService healthcareService = healthcareServiceList.get(0);

        assertThat(organization).isEqualTo(healthcareService.getProvidedByTarget());
        assertThat(HEALTHCARE_SERVICE_NAME).isEqualTo(healthcareService.getName());
        assertThat(true).isEqualTo(healthcareService.getActive());
        assertThat(healthcareServiceList).isNotEmpty();
        assertThat(healthcareService.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }

    @Test
    public void shouldMapHealthcareServiceWrongTypeCode() {
        when(informationRecipient.getTypeCode()).thenReturn(forString(TRC_TYPE_CODE));

        List<HealthcareService> healthcareServiceList = healthcareServiceMapper
            .mapHealthcareService(clinicalDocument);

        assertThat(healthcareServiceList).isEmpty();
    }
}
