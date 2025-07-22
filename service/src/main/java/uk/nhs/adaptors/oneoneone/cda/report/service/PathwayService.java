package uk.nhs.adaptors.oneoneone.cda.report.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlbeans.XmlObject;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument
    .PathwaysCase.PathwayDetails.PathwayTriageDetails
    .PathwayTriage.TriageLineDetails.TriageLine;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.QuestionnaireResponseMapper;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.StructuredBodyUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ObservationMedia;

@Component
@RequiredArgsConstructor
@Slf4j
public class PathwayService {
    private final NodeUtil nodeUtil;
    private final QuestionnaireResponseMapper questionnaireResponseMapper;

    public List<QuestionnaireResponse> getQuestionnaireResponses(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Reference patient,
        Reference encounter) {
        List<QuestionnaireResponse> questionnaireResponseList = new ArrayList<>();

        String pathwaysCase = findPathwaysCase(clinicalDocument);
        if (pathwaysCase != null) {
            try {
                var pathwaysCaseDocument = (PathwaysCaseDocument) XmlObject.Factory.parse(pathwaysCase);
                var documentPathwaysCase = pathwaysCaseDocument.getPathwaysCase();
                TriageLine[] triageLineArray = documentPathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0)
                    .getTriageLineDetails().getTriageLineArray();

                for (TriageLine triageLine : triageLineArray) {
                    questionnaireResponseList.add(questionnaireResponseMapper.mapQuestionnaireResponse(documentPathwaysCase, patient,
                        encounter, triageLine));
                }

                return questionnaireResponseList;
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred when parsing pathways case");
            }
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
            .map(s -> s.replaceAll("\"True\"", "\"true\""))
            .map(s -> s.replaceAll("\"False\"", "\"false\""))
            .orElse(null);
    }
}
