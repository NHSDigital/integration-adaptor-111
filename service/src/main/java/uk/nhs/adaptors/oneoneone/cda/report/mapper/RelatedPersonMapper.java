package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.stereotype.Component;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;

import java.util.Optional;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class RelatedPersonMapper {
    private PeriodMapper periodMapper;
    private PractitionerMapper practitionerMapper;

    public Optional<RelatedPerson> mapRelatedPerson(POCDMT000002UK01Informant12 informant,
                                                    Encounter encounter) {
        if (informant.isSetRelatedEntity()) {
            POCDMT000002UK01RelatedEntity relatedEntity = informant.getRelatedEntity();
            if (relatedEntity.isSetRelatedPerson()) {
                RelatedPerson relatedPerson = new RelatedPerson();
                relatedPerson.setIdElement(newRandomUuid());

                Practitioner practitioner = practitionerMapper.mapPractitioner(relatedEntity);
                relatedPerson.setBirthDate(practitioner.getBirthDate())
                             .setAddress(practitioner.getAddress())
                             .setGender(practitioner.getGender())
                             .setIdentifier(practitioner.getIdentifier())
                             .setName(practitioner.getName())
                             .setTelecom(practitioner.getTelecom())
                             .setActive(practitioner.getActive())
                             .setPatient(encounter.getSubject());

                if (relatedEntity.isSetEffectiveTime())
                    relatedPerson.setPeriod(periodMapper.mapPeriod(relatedEntity.getEffectiveTime()));

                return Optional.of(relatedPerson);
            }
        }

        return Optional.empty();
    }
}
