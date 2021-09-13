package uk.nhs.adaptors.oneoneone.config;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import static org.apache.commons.lang3.StringUtils.split;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "itk")
@Setter
public class ItkProperties {
    private String odsCodes;
    private String dosIds;
    @Getter
    private String externalConfigurationServiceUrl;
    @Getter
    private int fetchIntervalMinutes;

    public List<String> getOdsCodes() {
        return splitAndTrim(odsCodes);
    }

    public List<String> getDosIds() {
        return splitAndTrim(dosIds);
    }

    public void setOdsCodesDosIds(OdsCodesDosIds odsCodesDosIds) {
        odsCodes = odsCodesDosIds.getOdsCodes().stream().collect(joining(","));
        dosIds = odsCodesDosIds.getDosIds().stream().collect(joining(","));
    }

    private List<String> splitAndTrim(String list) {
        return stream(split(list, ","))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(toList());
    }
}
