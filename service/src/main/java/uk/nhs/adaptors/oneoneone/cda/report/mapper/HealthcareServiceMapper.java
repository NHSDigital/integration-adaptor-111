package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.TEL;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;


@Component
@RequiredArgsConstructor
public class HealthcareServiceMapper {
    private final LocationMapper locationMapper;
    private final OrganizationMapper organizationMapper;
    private final ContactPointMapper contactPointMapper;

    public HealthcareService mapHealthcareService(
            POCDMT000002UK01InformationRecipient informationRecipient) {

        POCDMT000002UK01IntendedRecipient intendedRecipient =
                informationRecipient.getIntendedRecipient();

        HealthcareService healthcareService = new HealthcareService()
                .setActive(true);

        healthcareService.setIdElement(newRandomUuid());

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
                healthcareService.setName(NodeUtil.getAllText(name.getDomNode()));
            }
        }

        return healthcareService;
    }
}
