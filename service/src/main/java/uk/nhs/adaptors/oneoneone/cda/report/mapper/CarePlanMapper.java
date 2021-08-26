package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.stream.Collectors.toUnmodifiableList;

import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent.PLAN;
import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus.COMPLETED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@Component
@AllArgsConstructor
public class CarePlanMapper {

    private final ConditionMapper conditionMapper;
    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public List<CarePlan> mapCarePlan(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter, Condition condition) {
        if (clinicalDocument.getComponent().isSetStructuredBody()) {
            POCDMT000002UK01StructuredBody structuredBody = getStructuredBody(clinicalDocument);

            return Arrays.stream(structuredBody.getComponentArray())
                .map(POCDMT000002UK01Component3::getSection)
                .map(this::findCarePlanSections)
                .flatMap(List::stream)
                .map(section -> createCarePlanFromSection(section, encounter, condition))
                .collect(toUnmodifiableList());
        } else {
            return Collections.emptyList();
        }
    }

    public CarePlan createCarePlanFromSection(POCDMT000002UK01Section cpSection, Encounter encounter, Condition condition) {
        CarePlan carePlan = new CarePlan();
        carePlan.setIdElement(resourceUtil.newRandomUuid());
        carePlan
            .setIntent(PLAN)
            .setSubject(encounter.getSubject())
            .setSubjectTarget(encounter.getSubjectTarget())
            .setStatus(COMPLETED)
            .setContextTarget(encounter)
            .setContext(resourceUtil.createReference(encounter))
            .setPeriod(encounter.getPeriod())
            .addAddresses(resourceUtil.createReference(condition));

        if (cpSection.isSetLanguageCode()) {
            carePlan.setLanguage(nodeUtil.getNodeValueString(cpSection.getLanguageCode()));
        }
        if (cpSection.isSetTitle()) {
            carePlan.setTitle(nodeUtil.getNodeValueString(cpSection.getTitle()));
        }

        if (cpSection.getText().sizeOfContentArray() > 0) {
            String cpTextContent = nodeUtil.getNodeValueString(cpSection.getText().getContentArray(0));
            carePlan.setDescription(cpTextContent);
        }

        if (encounter.hasLocation()) {
            List<Reference> authorList = new ArrayList<>();
            for (Encounter.EncounterLocationComponent author : encounter.getLocation()) {
                if (author.hasLocation()) {
                    Location location = (Location) author.getLocation().getResource();
                    if (location.hasManagingOrganization()) {
                        authorList.add(location.getManagingOrganization());
                    }
                }
            }
            carePlan.setAuthor(authorList);
        }

        return carePlan;
    }

    private POCDMT000002UK01StructuredBody getStructuredBody(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return Optional.ofNullable(clinicalDocument)
            .map(POCDMT000002UK01ClinicalDocument1::getComponent)
            .map(POCDMT000002UK01Component2::getStructuredBody)
            .orElse(null);
    }

    private List<POCDMT000002UK01Section> findCarePlanSections(POCDMT000002UK01Section section) {

        if (section.sizeOfComponentArray() == 0) {
            return Collections.emptyList();
        }

        List<POCDMT000002UK01Section> subSections = Arrays.stream(section.getComponentArray())
            .map(POCDMT000002UK01Component5::getSection)
            .collect(toUnmodifiableList());

        List<POCDMT000002UK01Section> carePlanSections = subSections.stream()
            .filter(this::isCareAdvice)
            .collect(Collectors.toList());

        subSections.stream()
            .map(this::findCarePlanSections)
            .forEach(carePlanSections::addAll);

        return carePlanSections;
    }

    private boolean isCareAdvice(POCDMT000002UK01Section section) {
        final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
        final String INFORMATION_ADVICE_GIVEN = "1052951000000105";

        CE code = section.getCode();
        return code != null
            && SNOMED.equals(code.getCodeSystem())
            && INFORMATION_ADVICE_GIVEN.equals(code.getCode());
    }
}
