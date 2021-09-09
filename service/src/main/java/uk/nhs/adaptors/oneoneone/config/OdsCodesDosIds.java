package uk.nhs.adaptors.oneoneone.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OdsCodesDosIds {
    @JsonProperty
    private List<String> odsCodes;
    @JsonProperty
    private List<String> dosIds;
}
