package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;

@Component
@AllArgsConstructor
public class PractitionerRoleMapper {

    public PractitionerRole mapPractitioner(POCDMT000002UK01AssignedAuthor assignedAuthor) {
        PractitionerRole practitionerRole = new PractitionerRole();
        CE assignedAuthorCode = assignedAuthor.getCode();

        practitionerRole.setIdElement(newRandomUuid());
        practitionerRole.addCode(
            new CodeableConcept().addCoding(
                new Coding()
                    .setSystem(assignedAuthorCode.getCodeSystem())
                    .setCode(assignedAuthorCode.getCode())
                    .setDisplay(assignedAuthorCode.getDisplayName())
            )
        );

        return practitionerRole;
    }
}
