package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.EnumOptions;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.w3c.dom.Node;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HealthcareServiceMapperTest {

    private static final String HEALTHCARE_SERVICE_NAME = "Thames Medical Practice";
    private static final String PRCP_TYPE_CODE = "PRCP";

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
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Organization organization;
    @Mock
    private Location location;

    @BeforeEach
    public void setup() {
        when(locationMapper.mapRecipientToLocation(intendedRecipient))
            .thenReturn(location);
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01InformationRecipient.class)))
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
        when(organization.getType().get(0).getCoding().get(0).getCode()).thenReturn(PRCP_TYPE_CODE);


    }

    @Test
    public void shouldMapHealthcareService() {

        List<HealthcareService> healthcareServiceList = healthcareServiceMapper
            .mapHealthcareService(clinicalDocument);

        HealthcareService healthcareService = healthcareServiceList.get(0);
        assertThat(organization).isEqualTo(healthcareService.getProvidedByTarget());
        assertThat(HEALTHCARE_SERVICE_NAME).isEqualTo(healthcareService.getName());
        assertThat(true).isEqualTo(healthcareService.getActive());
    }
}
