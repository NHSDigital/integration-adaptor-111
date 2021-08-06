package uk.nhs.adaptors.oneoneone.cda.report.enums;

import org.hl7.fhir.dstu3.model.Coding;

import lombok.Getter;

@Getter
public enum MessageHeaderEvent {
    REFERRAL("referral-1", "Referral"),
    DISCHARGE_DETAILS("discharge-details-1", "Discharge Details");

    public static final String SYSTEM = "https://fhir.nhs.uk/STU3/CodeSystem/EventType-1";
    private String code;
    private String display;

    MessageHeaderEvent(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public Coding toCoding() {
        return new Coding()
            .setSystem(SYSTEM)
            .setCode(code)
            .setDisplay(display);
    }
}
