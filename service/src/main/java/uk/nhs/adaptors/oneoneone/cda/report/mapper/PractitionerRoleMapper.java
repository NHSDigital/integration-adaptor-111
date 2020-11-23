package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ResponsibleParty;

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
            role.setCode(asList(getCode(assignedAuthor.getCode())));
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

    public Optional<PractitionerRole> mapResponsibleParty(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.isSetComponentOf()) {
            POCDMT000002UK01Component1 componentOf = clinicalDocument.getComponentOf();
            if (componentOf.getEncompassingEncounter().isSetResponsibleParty()) {
                PractitionerRole role = new PractitionerRole();
                role.setIdElement(newRandomUuid());
                POCDMT000002UK01ResponsibleParty responsibleParty = componentOf.getEncompassingEncounter().getResponsibleParty();
                POCDMT000002UK01AssignedEntity assignedEntity = responsibleParty.getAssignedEntity();
                role.setCode(asList(getCode(assignedEntity.getCode())));
                if (assignedEntity.isSetRepresentedOrganization()) {
                    Organization organization = organizationMapper.mapOrganization(assignedEntity.getRepresentedOrganization());
                    role.setOrganization(new Reference(organization));
                    role.setOrganizationTarget(organization);
                }
                Practitioner practitioner = practitionerMapper.mapPractitioner(assignedEntity);
                role.setPractitioner(new Reference(practitioner));
                role.setPractitionerTarget(practitioner);
                return Optional.of(role);
            }
        }
        return empty();
    }

    private CodeableConcept getCode(CE code) {
        return new CodeableConcept(new Coding()
            .setCode(code.getCode())
            .setSystem(code.getCodeSystem())
            .setDisplay(code.getDisplayName()));
    }
}
