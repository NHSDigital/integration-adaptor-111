package uk.nhs.adaptors.oneoneone.cda.report.util;

import lombok.experimental.UtilityClass;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class PathwaysUtil {

    public Optional<PathwaysCaseDocument.PathwaysCase.Outcome> getOutcome(PathwaysCaseDocument.PathwaysCase pathwaysCase) {
        if (pathwaysCase == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(pathwaysCase.getOutcome());
    }

    public List<PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine> getAllTriageLines(PathwaysCaseDocument.PathwaysCase pathwaysCase) {
        return Optional.ofNullable(pathwaysCase)
                .map(PathwaysCaseDocument.PathwaysCase::getPathwayDetails)
                .map(PathwaysCaseDocument.PathwaysCase.PathwayDetails::getPathwayTriageDetails)
                .map(PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails::getPathwayTriageArray)
                .stream()
                .flatMap(Stream::of)
                .map(PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage::getTriageLineDetails)
                .map(PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails::getTriageLineArray)
                .flatMap(Stream::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<Calendar> getLastTriageLineFinishDate(PathwaysCaseDocument.PathwaysCase pathwaysCase) {
        List<PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine> triageLines = PathwaysUtil.getAllTriageLines(pathwaysCase);

        ListIterator<PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine> iterator = triageLines.listIterator(triageLines.size());

        while (iterator.hasPrevious()) {
            PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine previous = iterator.previous();
            if (previous.getFinish() != null) {
                return Optional.of(previous.getFinish());
            }
        }
        return Optional.empty();
    }

}

