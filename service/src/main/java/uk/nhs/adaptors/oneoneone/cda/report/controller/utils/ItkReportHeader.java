package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItkReportHeader {
    private String trackingId;
    private String specKey;
    private String specVal;
    private List<String> addressList;
}
