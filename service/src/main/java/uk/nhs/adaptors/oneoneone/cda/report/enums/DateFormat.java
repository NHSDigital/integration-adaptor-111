package uk.nhs.adaptors.oneoneone.cda.report.enums;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import lombok.Getter;

@Getter
public class DateFormat {
    private String dateFormat;
    private TemporalPrecisionEnum precision;

    public DateFormat(String dateFormat, TemporalPrecisionEnum precision) {
        this.dateFormat = dateFormat;
        this.precision = precision;
    }
}
