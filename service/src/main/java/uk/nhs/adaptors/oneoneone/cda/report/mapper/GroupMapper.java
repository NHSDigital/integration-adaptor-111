package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;

import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class GroupMapper {

    private PatientMapper patientMapper;

    public Group mapGroup(POCDMT000002UK01RecordTarget[] recordTargetArr) {
        Group group = new Group();
        group.setIdElement(newRandomUuid());
        group.setActive(true);
        group.setType(Group.GroupType.PERSON);

        for (POCDMT000002UK01RecordTarget recordTarget : recordTargetArr) {
            Group.GroupMemberComponent groupMemberComponent = new Group.GroupMemberComponent();
            if (recordTarget.getPatientRole() != null) {
                Patient fhirPatient = patientMapper.mapPatient(recordTarget.getPatientRole());
                groupMemberComponent.setEntity(new Reference(fhirPatient));
                groupMemberComponent.setEntityTarget(fhirPatient);
                group.addMember(groupMemberComponent);
            }
        }

        return group;
    }
}
