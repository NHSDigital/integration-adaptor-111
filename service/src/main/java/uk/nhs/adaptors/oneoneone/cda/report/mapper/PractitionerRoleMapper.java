package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;

@Component
@AllArgsConstructor
public class PractitionerRoleMapper {
    private final OrganizationMapper organizationMapper;
    private final PractitionerMapper practitionerMapper;

    public List<PractitionerRole> mapAuthorRoles(POCDMT000002UK01Author[] authors) {
        List<PractitionerRole> roles = new ArrayList<>();
        stream(authors).map(author -> {
            PractitionerRole role = new PractitionerRole();
            role.setIdElement(newRandomUuid());
            POCDMT000002UK01AssignedAuthor assignedAuthor = author.getAssignedAuthor();
            role.setCode(asList(getCode(assignedAuthor)));
            Organization organization = organizationMapper.mapOrganization(assignedAuthor.getRepresentedOrganization());
            role.setOrganization(new Reference(organization));
            role.setOrganizationTarget(organization);
            Practitioner practitioner = practitionerMapper.mapPractitioner(assignedAuthor);
            role.setPractitioner(new Reference(practitioner));
            role.setPractitionerTarget(practitioner);
            return role;
        })
            .forEach(roles::add);

        return roles;
    }

    private CodeableConcept getCode(POCDMT000002UK01AssignedAuthor author) {
        CE code = author.getCode();
        return new CodeableConcept(new Coding()
            .setCode(code.getCode())
            .setSystem(code.getCodeSystem())
            .setDisplay(code.getDisplayName()));
    }
}
