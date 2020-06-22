package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import java.util.List;
import java.util.stream.Collectors;

public class NarrativeService {
    private static final String LINE_BREAK = "<br />";
    private static final String DIV_START = "<div>";
    private static final String DIV_END = "</div>";

    public Narrative buildNarrative(String text) {
        var narrative = new Narrative().setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDivAsString(text);
        return narrative;
    }

    public Narrative buildNarrative(List<String> lines) {
        return buildNarrative(String.join(LINE_BREAK, lines));
    }

    public Narrative buildCombinedNarrative(List<Narrative> narratives) {
        return buildNarrative(narratives.stream()
                .filter(Narrative::hasDiv)
                .map(Narrative::getDiv)
                .map(XhtmlNode::toString)
                .collect(Collectors.joining("", DIV_START, DIV_END)));
    }
}
