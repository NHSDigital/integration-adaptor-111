package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import static org.apache.logging.log4j.util.Strings.join;
import static org.hl7.fhir.dstu3.model.Observation.ObservationStatus.FINAL;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.StrucDocContent;

@Component
@AllArgsConstructor
public class ObservationMapper {

    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String PRESENTING_COMPLAINT_DISPLAY = "Presenting complaint";
    private static final String PRESENTING_COMPLAINT_CODE = "33962009";
    private static final String PATIENTS_CONDITION_REGEXP = "Patient.s Reported Condition";
    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public List<Observation> mapObservations(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        List<Observation> observations = new ArrayList<>();

        POCDMT000002UK01Component2 component = clinicalDocument.getComponent();
        if (component.isSetStructuredBody()) {
            POCDMT000002UK01Component3[] components = component.getStructuredBody().getComponentArray();
            for (POCDMT000002UK01Component3 component3 : components) {
                POCDMT000002UK01Section section = component3.getSection();
                for (POCDMT000002UK01Component5 component5 : section.getComponentArray()) {
                    ST title = component5.getSection().getTitle();
                    if (nodeUtil.getNodeValueString(title).matches(PATIENTS_CONDITION_REGEXP)) {
                        StrucDocContent[] contentArray = component5.getSection().getText().getContentArray();
                        List<String> sectionText =
                            stream(contentArray)
                                .map(it -> nodeUtil.getNodeValueString(it))
                                .collect(toList());

                        observations.add(createObservation(encounter, sectionText));
                    }
                }
            }
        }

        return observations;
    }

    private Observation createObservation(Encounter encounter, List<String> sectionText) {
        Observation observation = new Observation();
        observation.setIdElement(resourceUtil.newRandomUuid());
        observation.setStatus(FINAL);
        Coding coding = new Coding()
            .setCode(PRESENTING_COMPLAINT_CODE)
            .setDisplay(PRESENTING_COMPLAINT_DISPLAY)
            .setSystem(SNOMED_SYSTEM);
        observation.setCode(new CodeableConcept(coding));
        observation.setValue(new StringType(join(sectionText, '\n')));
        observation.setContext(resourceUtil.createReference(encounter));
        observation.setSubject(encounter.getSubject());
        return observation;
    }
}
