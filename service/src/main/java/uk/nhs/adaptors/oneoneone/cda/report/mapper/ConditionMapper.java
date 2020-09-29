package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

@Component
@AllArgsConstructor
public class ConditionMapper {

    private final NodeUtil nodeUtil;

    public Condition mapCondition(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        Condition condition = new Condition();

        condition.setIdElement(newRandomUuid());

        condition
            .setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE)
            .setVerificationStatus(Condition.ConditionVerificationStatus.UNKNOWN)
            .setSubject(encounter.getSubject())
            .setContext(new Reference(encounter))
            .setAsserter(encounter.getParticipantFirstRep().getIndividual());

        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            for (POCDMT000002UK01Component3 component3 : clinicalDocument.getComponent().getStructuredBody().getComponentArray()) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Entry entry : section.getEntryArray()) {
                    if (entry.isSetEncounter()) {
                        POCDMT000002UK01Encounter itkEncounter = entry.getEncounter();
                        if (itkEncounter.isSetEffectiveTime()) {
                            condition
                                .setAssertedDate(DateUtil.parse(itkEncounter.getEffectiveTime().getValue()));
                        }
                        if (itkEncounter.isSetText()) {
                            condition
                                .addCategory(new CodeableConcept().setText(
                                    nodeUtil.getAllText(itkEncounter.getText().getDomNode())));
                        }
                    }
                }
            }
        }

        return condition;
    }
}
