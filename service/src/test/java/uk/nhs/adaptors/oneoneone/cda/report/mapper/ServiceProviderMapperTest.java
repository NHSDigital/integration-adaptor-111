package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.dstu3.model.Organization;
import org.junit.Test;

public class ServiceProviderMapperTest {

    private ServiceProviderMapper serviceProviderMapper = new ServiceProviderMapper();

    @Test
    public void mapEpisodeOfCare() {
        Organization organization = serviceProviderMapper.mapServiceProvider();

        assertThat(organization.getIdElement().getValue()).startsWith("urn:uuid:");
    }
}
