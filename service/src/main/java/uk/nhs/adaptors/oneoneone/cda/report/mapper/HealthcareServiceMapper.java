package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.ON;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01IntendedRecipient;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.TEL;

@Component
@RequiredArgsConstructor
public class HealthcareServiceMapper {

    private static final String PRCP = "PRCP";
    private final LocationMapper locationMapper;
    private final OrganizationMapper organizationMapper;
    private final ContactPointMapper contactPointMapper;
    private final NodeUtil nodeUtil;

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
        informationRecipient.getTypeCode();

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
            POCDMT000002UK01Organization receivedOrganization = intendedRecipient.getReceivedOrganization();
            Organization organization = organizationMapper.mapOrganization(receivedOrganization);
            Coding code = new Coding().setCode(String.valueOf(informationRecipient.getTypeCode()));
            if (code.getCode() == PRCP) {
                organization.setType(Collections.singletonList(new CodeableConcept(code)));
                Coding display = new Coding().setDisplay(nodeUtil.getAllText(receivedOrganization.getDomNode()));
                organization.setType(Collections.singletonList(new CodeableConcept(display)));
            }
                healthcareService.setProvidedBy(new Reference(organization));
                healthcareService.setProvidedByTarget(organization);
                if (receivedOrganization.sizeOfNameArray() > 0) {
                    ON name = receivedOrganization.getNameArray(0);
                    healthcareService.setName(nodeUtil.getAllText(name.getDomNode()));
                }
            //}
        }

        return healthcareService;
    }
}
