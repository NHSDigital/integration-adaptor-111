package uk.nhs.adaptors.oneoneone.cda.report.mapper;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthcareServiceMapperTest {

    private static final String HEALTHCARE_SERVICE_NAME = "Thames Medical Practice";
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
        when(locationMapper.mapRecipientToLocation(intendedRecipient))
            .thenReturn(location);
        when(organizationMapper.mapOrganization(any()))
            .thenReturn(organization);
        POCDMT000002UK01InformationRecipient[] informationRecipientArray = new POCDMT000002UK01InformationRecipient[1];
        informationRecipientArray[0] = informationRecipient;
        when(clinicalDocument.getInformationRecipientArray()).thenReturn(informationRecipientArray);
        when(informationRecipient.getIntendedRecipient()).thenReturn(intendedRecipient);
        when(intendedRecipient.isSetReceivedOrganization()).thenReturn(true);
        when(intendedRecipient.getReceivedOrganization()).thenReturn(receivedOrganization);
        when(receivedOrganization.sizeOfNameArray()).thenReturn(1);
        when(receivedOrganization.getNameArray(0)).thenReturn(name);
        when(name.getDomNode()).thenReturn(node);
        when(nodeUtil.getAllText(name.getDomNode())).thenReturn(HEALTHCARE_SERVICE_NAME);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
    }

    @Test
    public void shouldMapHealthcareService() {
        List<HealthcareService> healthcareServiceList = healthcareServiceMapper
            .mapHealthcareService(clinicalDocument);

        HealthcareService healthcareService = healthcareServiceList.get(0);

        assertThat(organization).isEqualTo(healthcareService.getProvidedByTarget());
        assertThat(HEALTHCARE_SERVICE_NAME).isEqualTo(healthcareService.getName());
        assertThat(true).isEqualTo(healthcareService.getActive());
        assertThat(healthcareService.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }
}
