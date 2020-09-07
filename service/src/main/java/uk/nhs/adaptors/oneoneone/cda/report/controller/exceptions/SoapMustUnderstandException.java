package uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions;

import lombok.Getter;

@Getter
public class SoapMustUnderstandException extends Exception {
    private final String reason;

    public SoapMustUnderstandException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
