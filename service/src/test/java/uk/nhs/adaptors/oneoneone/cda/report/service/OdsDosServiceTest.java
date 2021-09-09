package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import uk.nhs.adaptors.oneoneone.config.ItkProperties;
import uk.nhs.adaptors.oneoneone.config.OdsCodesDosIds;

@ExtendWith(MockitoExtension.class)
public class OdsDosServiceTest {

    private static final String EXTERNAL_SERVICE_URL = "http://external-conf-service.domain.com/configuration";
    @Mock
    private ItkProperties itkProperties;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OdsCodesDosIds odsCodesDosIds;
    @InjectMocks
    private OdsDosService odsDosService;

    @BeforeEach
    public void setUp() {
        when(itkProperties.getExternalConfigurationServiceUrl()).thenReturn(EXTERNAL_SERVICE_URL);
        ResponseEntity mock = mock(ResponseEntity.class);
        when(mock.getBody()).thenReturn(odsCodesDosIds);
        when(restTemplate.getForEntity(EXTERNAL_SERVICE_URL, OdsCodesDosIds.class)).thenReturn(mock);
    }

    @Test
    public void shouldFetchOdsCodeAndDosIds() {
        OdsCodesDosIds fetchedOdsDos = odsDosService.fetchOdsCodeAndDosIds();

        assertThat(fetchedOdsDos).isEqualTo(odsCodesDosIds);
    }
}
