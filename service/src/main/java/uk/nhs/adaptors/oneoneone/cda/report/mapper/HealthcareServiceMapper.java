package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@RequiredArgsConstructor
public class HealthcareServiceMapper {
    private final LocationMapper locationMapper;
    private final OrganizationMapper organizationMapper;
    private final ContactPointMapper contactPointMapper;
    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public List<HealthcareService> mapHealthcareService(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {

        List<HealthcareService> healthcareServiceList = new ArrayList<>();

        for (POCDMT000002UK01InformationRecipient recipient : clinicalDocument.getInformationRecipientArray()) {
            healthcareServiceList.add(mapSingleHealthcareService(recipient));
        }

        return healthcareServiceList;
    }

    private HealthcareService mapSingleHealthcareService(
        POCDMT000002UK01InformationRecipient informationRecipient) {

        POCDMT000002UK01IntendedRecipient intendedRecipient =
            informationRecipient.getIntendedRecipient();

        HealthcareService healthcareService = new HealthcareService()
            .setActive(true);

        healthcareService.setIdElement(resourceUtil.newRandomUuid());

        Location location = locationMapper.mapRecipientToLocation(intendedRecipient);
        healthcareService.addLocation(new Reference(location));

        if (intendedRecipient.sizeOfTelecomArray() > 0) {
            for (TEL tel : intendedRecipient.getTelecomArray()) {
                healthcareService.addTelecom(contactPointMapper.mapContactPoint(tel));
            }
        }

        if (intendedRecipient.isSetReceivedOrganization()) {
            POCDMT000002UK01Organization receivedOrganization =
                intendedRecipient.getReceivedOrganization();
            Organization organization = organizationMapper.mapOrganization(receivedOrganization);
            healthcareService.setProvidedBy(new Reference(organization));
            healthcareService.setProvidedByTarget(organization);
            if (receivedOrganization.sizeOfNameArray() > 0) {
                ON name = receivedOrganization.getNameArray(0);
                healthcareService.setName(nodeUtil.getAllText(name.getDomNode()));
            }
        }

        return healthcareService;
    }
}
