package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus.ACTIVE;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class EpisodeOfCareMapper {

    private PractitionerMapper practitionerMapper;
    private OrganizationMapper organizationMapper;
    private PeriodMapper periodMapper;

    public Optional<EpisodeOfCare> mapEpisodeOfCare(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Reference subject) {
        POCDMT000002UK01EncompassingEncounter encompassingEncounter = clinicalDocument.getComponentOf()
                .getEncompassingEncounter();

        if (encompassingEncounter.isSetResponsibleParty() &&
                encompassingEncounter.getResponsibleParty().getAssignedEntity() != null) {
            POCDMT000002UK01AssignedEntity assignedEntity = encompassingEncounter.getResponsibleParty().getAssignedEntity();
            EpisodeOfCare episodeOfCare = new EpisodeOfCare();
            episodeOfCare.setPatient(subject);
            episodeOfCare.setStatus(ACTIVE);
            episodeOfCare.setType(asList(new CodeableConcept().setText(encompassingEncounter.getCode().getCode())));
            episodeOfCare.setPeriod(periodMapper.mapPeriod(encompassingEncounter.getEffectiveTime()));
            episodeOfCare.setId(newRandomUuid());
            Practitioner practitioner = practitionerMapper.mapPractitioner(assignedEntity);
            episodeOfCare.setCareManagerTarget(practitioner);
            episodeOfCare.setCareManager(new Reference(practitioner));

            if (assignedEntity.isSetRepresentedOrganization()) {
                POCDMT000002UK01Organization representedOrganization = assignedEntity
                        .getRepresentedOrganization();

                Organization organization = organizationMapper.mapOrganization(representedOrganization);
                episodeOfCare.setManagingOrganization(new Reference(organization));
                episodeOfCare.setManagingOrganizationTarget(organization);
            }

            return Optional.of(episodeOfCare);
        } else {
            return Optional.empty();
        }
    }
}
