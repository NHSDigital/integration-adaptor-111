package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.QuestionnaireResponseMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ObservationMedia;

@Component
@RequiredArgsConstructor
public class PathwayUtil {
    private final NodeUtil nodeUtil;
    private final QuestionnaireResponseMapper questionnaireResponseMapper;

    public List<QuestionnaireResponse> getQuestionnaireResponses(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Reference patient,
        Reference encounter) throws XmlException {
        List<QuestionnaireResponse> questionnaireResponseList = new ArrayList<>();

        String pathwaysCase = findPathwaysCase(clinicalDocument);
        if (pathwaysCase != null) {
            PathwaysCase pathwaysCaseDocument = PathwaysCaseDocument.Factory.parse(pathwaysCase).getPathwaysCase();
            TriageLine[] triageLineArray = pathwaysCaseDocument.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0)
                .getTriageLineDetails().getTriageLineArray();

            for (TriageLine traigeLine : triageLineArray) {
                questionnaireResponseList.add(questionnaireResponseMapper.mapQuestionnaireResponse(pathwaysCaseDocument, patient,
                    encounter, traigeLine));
            }

            return questionnaireResponseList;
        }
        return null;
    }

    private String findPathwaysCase(POCDMT000002UK01ClinicalDocument1 document) {
        return StructuredBodyUtil.getEntriesOfType(
            StructuredBodyUtil.getStructuredBody(document),
            "COCD_TP146002GB01#ObservationMedia")
            .stream()
            .findFirst()
            .map(POCDMT000002UK01Entry::getObservationMedia)
            .map(POCDMT000002UK01ObservationMedia::getValue)
            .map(nodeUtil::getNodeValueString)
            .map(String::strip)
            .map(String::getBytes)
            .map(Base64::decodeBase64)
            .map(String::new)
            .orElse(null);
    }
}
