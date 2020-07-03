package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class DiagnosisMapper {

    private ConditionMapper conditionMapper;

    public DiagnosisComponent mapDiagnosis(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        DiagnosisComponent diagnosis = new DiagnosisComponent();

        diagnosis.setId(String.valueOf(newRandomUuid()));

        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Entry entry : section.getEntryArray()) {
                    if (entry.isSetEncounter()) {
                        POCDMT000002UK01Encounter encounter = entry.getEncounter();
                        if (encounter.isSetText()) {
                            Condition condition = conditionMapper.mapCondition(encounter);
                            diagnosis.setCondition(new Reference(condition));
                            diagnosis.setConditionTarget(condition);
                        }
                        if (encounter.isSetStatusCode()) {
                            diagnosis.setRole(new CodeableConcept().setText(encounter.getStatusCode().getCode()));
                        }
                        if (encounter.isSetCode()) {
                            diagnosis.setRank(Integer.parseInt(encounter.getPriorityCode().getCode()));
                        }
                    }
                }
            }
        }

        return diagnosis;
    }
}
