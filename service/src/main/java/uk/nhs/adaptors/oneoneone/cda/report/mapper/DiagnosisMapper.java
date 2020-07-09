package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

@Component
@AllArgsConstructor
public class DiagnosisMapper {

    private final ConditionMapper conditionMapper;

    public DiagnosisComponent mapDiagnosis(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        DiagnosisComponent diagnosis = new DiagnosisComponent();

        diagnosis.setId(String.valueOf(newRandomUuid()));

        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Entry entry : section.getEntryArray()) {
                    if (entry.isSetEncounter()) {
                        POCDMT000002UK01Encounter itkEncounter = entry.getEncounter();
                        if (itkEncounter.isSetText()) {
                            Condition condition = conditionMapper.mapCondition(itkEncounter, encounter);
                            diagnosis.setCondition(new Reference(condition));
                            diagnosis.setConditionTarget(condition);
                        }
                        if (itkEncounter.isSetStatusCode()) {
                            diagnosis.setRole(new CodeableConcept().setText(itkEncounter.getStatusCode().getCode()));
                        }
                        if (itkEncounter.isSetCode()) {
                            diagnosis.setRank(Integer.parseInt(itkEncounter.getPriorityCode().getCode()));
                        }
                    }
                }
            }
        }

        return diagnosis;
    }
}
