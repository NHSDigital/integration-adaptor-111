package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.*;

public class GenderMapperTest {

    @Test
    public void mapGenderTest() {
        assertThat(GenderMapper.getGenderFromCode("1")).isEqualTo(MALE);
        assertThat(GenderMapper.getGenderFromCode("2")).isEqualTo(FEMALE);
        assertThat(GenderMapper.getGenderFromCode("9")).isEqualTo(OTHER);
        assertThat(GenderMapper.getGenderFromCode(null)).isEqualTo(UNKNOWN);
        assertThat(GenderMapper.getGenderFromCode("0")).isEqualTo(UNKNOWN);
        assertThat(GenderMapper.getGenderFromCode("41")).isEqualTo(UNKNOWN);
        assertThat(GenderMapper.getGenderFromCode("abcd")).isEqualTo(UNKNOWN);
    }
}
