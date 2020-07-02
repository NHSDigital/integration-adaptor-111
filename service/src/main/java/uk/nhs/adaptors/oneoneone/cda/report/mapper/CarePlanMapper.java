package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent.PLAN;
import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus.ACTIVE;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class CarePlanMapper {
    private final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private final String INFORMATION_ADVICE_GIVEN = "1052951000000105";

    private List<CarePlan> carePlansCreated = new ArrayList<>();

    public List<CarePlan> mapCarePlan(POCDMT000002UK01ClinicalDocument1 clinicalDocument,
                                      Encounter encounter) {

        POCDMT000002UK01StructuredBody structuredBody = getStructuredBody(clinicalDocument);

        return Arrays.stream(structuredBody.getComponentArray())
                .map(POCDMT000002UK01Component3::getSection)
                .map(this::findCarePlanSections)
                .flatMap(List::stream)
                .map(section -> createCarePlanFromSection(section, encounter))
                .collect(Collectors.toUnmodifiableList());
    }

    public CarePlan createCarePlanFromSection(POCDMT000002UK01Section cpSection, Encounter encounter) {
        CarePlan carePlan = new CarePlan();
        carePlan.setIdElement(newRandomUuid());
        carePlan
                .setIntent(PLAN)
                .setSubject(encounter.getSubject())
                .setSubjectTarget(encounter.getSubjectTarget())
                .setStatus(ACTIVE)
                .setContextTarget(encounter)
                .setContext(new Reference(encounter))
                .setPeriod(encounter.getPeriod());

        if (cpSection.isSetLanguageCode()) {
            carePlan.setLanguage(NodeUtil.getNodeValueString(cpSection.getLanguageCode()));
        }
        if (cpSection.isSetTitle()) {
            carePlan.setTitle(NodeUtil.getNodeValueString(cpSection.getTitle()));
        }

        if (cpSection.getText().sizeOfContentArray() > 0) {
            String cpTextContent = NodeUtil.getNodeValueString(cpSection.getText().getContentArray(0));
            Narrative narrative = new Narrative();
            narrative.setDivAsString(cpTextContent);
            narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
            if (cpSection.isSetText()) {
                carePlan.setText(narrative);
            }
            carePlan.setDescription(cpTextContent);
        }

        // Missing from the data
//        getSupportingInfoFromITK(carePlan);

        return carePlan;
    }

    private void getSupportingInfoFromITK(CarePlan carePlan) {
        carePlansCreated.stream()
                .filter(ofTypes(QuestionnaireResponse.class, Observation.class))
                .map(Resource::getIdElement)
                .map(Reference::new)
                .forEach(carePlan::addSupportingInfo);
    }

    private POCDMT000002UK01StructuredBody getStructuredBody(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return Optional.ofNullable(clinicalDocument)
                .map(POCDMT000002UK01ClinicalDocument1::getComponent)
                .map(POCDMT000002UK01Component2::getStructuredBody)
                .orElse(null);
    }

    @SafeVarargs
    public static Predicate<DomainResource> ofTypes(Class<? extends DomainResource>... types) {
        return Arrays.stream(types)
                .map(type -> (Predicate<DomainResource>) type::isInstance)
                .reduce(Predicate::or)
                .orElse(x -> false);

    }

    private List<POCDMT000002UK01Section> findCarePlanSections(POCDMT000002UK01Section section) {

        if (section.sizeOfComponentArray() == 0) {
            return Collections.emptyList();
        }

        List<POCDMT000002UK01Section> subSections = Arrays.stream(section.getComponentArray())
                .map(POCDMT000002UK01Component5::getSection)
                .collect(Collectors.toUnmodifiableList());

        List<POCDMT000002UK01Section> carePlanSections = subSections.stream()
                .filter(this::isCareAdvice)
                .collect(Collectors.toList());

        subSections.stream()
                .map(this::findCarePlanSections)
                .forEach(carePlanSections::addAll);

        return carePlanSections;
    }

    private boolean isCareAdvice(POCDMT000002UK01Section section) {
        CE code = section.getCode();
        return code != null &&
                SNOMED.equals(code.getCodeSystem()) &&
                INFORMATION_ADVICE_GIVEN.equals(code.getCode());
    }
}
