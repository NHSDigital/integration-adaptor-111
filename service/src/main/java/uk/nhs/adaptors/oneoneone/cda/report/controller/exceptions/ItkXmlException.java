package uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions;

import lombok.Getter;

@Getter
public class ItkXmlException extends Exception {
    private final String reason;

    public ItkXmlException(String message, String reason, Throwable err) {
        super(message, err);
        this.reason = reason;
    }
}
