package uk.nhs.adaptors.oneoneone.cda.report.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceComparator implements Comparator<Resource>, Serializable {
    @Override
    public int compare(Resource resource1, Resource resource2) {
        if (resource1 == null && resource2 == null) {
            return 0;
        }

        if (resource1 == null ^ resource2 == null) {
            return (resource1 == null) ? -1 : 1;
        }

        Date date1 = getResourceDate(resource1);
        Date date2 = getResourceDate(resource2);

        if (date1 == null && date2 == null) {
            return 0;
        }

        if (date1 == null ^ date2 == null) {
            return (date1 == null) ? -1 : 1;
        }

        return date1.compareTo(date2);
    }

    private Date getResourceDate(Resource resource) {
        Date date = null;

        switch (resource.getResourceType().name()) {
            case "Condition":
                return ((Condition) resource).getAssertedDate();
            case "Questionnaire":
                return ((Questionnaire) resource).getDate();
            case "QuestionnaireResponse":
                return ((QuestionnaireResponse) resource).getAuthored();
            case "Observation":
                return date;
            case "Organization":
                return date;
            case "Practitioner":
                return date;
            case "Provenance":
                return ((Provenance) resource).getPeriod().getEnd();
            case "ReferralRequest":
                return ((ReferralRequest) resource).getAuthoredOn();
            case "RelatedPerson":
                return ((RelatedPerson) resource).getPeriod().getEnd();
            default:
                return date;
        }
    }
}
