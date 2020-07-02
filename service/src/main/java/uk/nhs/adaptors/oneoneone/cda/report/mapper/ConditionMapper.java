package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class ConditionMapper {

    public Condition mapCondition(POCDMT000002UK01Encounter encounter) {
        //stub class for NIAD-352: MAP Condition
        Condition condition = new Condition();

        condition.setIdElement(newRandomUuid());

        condition
                .setClinicalStatus(Condition.ConditionClinicalStatus.NULL)
                .setVerificationStatus(Condition.ConditionVerificationStatus.UNKNOWN);

        if (encounter.getText() != null) {
            condition
                    .addCategory(new CodeableConcept().setText(encounter.getText().xmlText()));
        }

        return condition;
    }
}
