package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Encounter;
import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
public class ReportMapper {
    public Encounter mapReport(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return null;
    }
}
