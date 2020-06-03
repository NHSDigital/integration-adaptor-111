package uk.nhs.adaptors.oneoneone.cda.report.mapper.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;

public class Verifiers {

    public static void verifyContactPoint(List<ContactPoint> actual, List<ContactPoint> expected) {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 1; i < actual.size(); i++) {
            assertThat(actual.get(i).getValue()).isEqualTo(expected.get(i).getValue());
            assertThat(actual.get(i).getUse().toCode()).isEqualTo(expected.get(i).getUse().toCode());
        }
    }

    public static void verifyHumanName(List<HumanName> actual, List<HumanName> expected) {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 1; i < actual.size(); i++) {
            assertThat(actual.get(i).getFamily()).isEqualTo(expected.get(i).getFamily());
            assertThat(actual.get(i).getGiven().toString()).isEqualTo(expected.get(i).getGiven().toString());
            assertThat(actual.get(i).getSuffix()).isEqualTo(expected.get(i).getSuffix());
            assertThat(actual.get(i).getPrefix()).isEqualTo(expected.get(i).getPrefix());
        }
    }

    public static void verifyPeriod(Period actual, Period expected) {
        assertThat(actual.getStart()).isEqualTo(expected.getStart());
        assertThat(actual.getEnd()).isEqualTo(expected.getEnd());
    }
}
