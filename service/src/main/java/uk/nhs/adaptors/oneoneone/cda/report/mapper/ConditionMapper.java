package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.Date;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;

@Component
@AllArgsConstructor
public class ConditionMapper {

    private final NodeUtil nodeUtil;

    public Condition mapCondition(POCDMT000002UK01Encounter itkEncounter, Encounter encounter) {
        Condition condition = new Condition();

        condition.setIdElement(newRandomUuid());

        Date assertedDate = DateUtil.parse(itkEncounter.getEffectiveTime().getValue());

        condition
            .setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE)
            .setVerificationStatus(Condition.ConditionVerificationStatus.UNKNOWN)
            .setSubject(encounter.getSubject())
            .setContext(new Reference(encounter))
            .setAsserter(encounter.getParticipantFirstRep().getIndividual())
            .setAssertedDate(assertedDate);

        if (itkEncounter.getText() != null) {
            condition
                .addCategory(new CodeableConcept().setText(
                    nodeUtil.getAllText(itkEncounter.getText().getDomNode())));
        }

        return condition;
    }
}
