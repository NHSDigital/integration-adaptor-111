package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.springframework.stereotype.Service;

@Service
public class PathwayUtil {
    public boolean isSetCaseReceiveEnd(PathwaysCase pathwaysCase) {
        // This try-catch is required because pathwaysCase.isSetCaseReceiveEnd() returns true
        // for <caseReceiveEnd/> element and fails when trying to cast it to Calendar type
        try {
            pathwaysCase.getCaseReceiveEnd();
        } catch (XmlValueOutOfRangeException exception) {
            return false;
        }
        return pathwaysCase.isSetCaseReceiveEnd();
    }
}
