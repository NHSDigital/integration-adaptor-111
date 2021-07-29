package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.FEMALE;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.MALE;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.OTHER;
import static org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender.UNKNOWN;

import org.junit.jupiter.api.Test;

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
