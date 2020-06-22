package uk.nhs.adaptors.oneoneone.cda.report.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Service;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.HealthcareServiceMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InformationRecipient;

@Service
@RequiredArgsConstructor
public class HealthcareServiceService {
    private final HealthcareServiceMapper healthcareServiceTransformer;

    public Reference createHealthcareService(POCDMT000002UK01InformationRecipient recipient) {
        HealthcareService healthcareService =
                healthcareServiceTransformer.transformRecipient(recipient);

        return createHealthcareService(healthcareService);
    }

    public Reference createHealthcareService(HealthcareService healthcareService) {
        return new Reference(healthcareService);
    }
}
