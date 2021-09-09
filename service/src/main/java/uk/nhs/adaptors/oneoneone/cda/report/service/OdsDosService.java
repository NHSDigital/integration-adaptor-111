package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;
import uk.nhs.adaptors.oneoneone.config.OdsCodesDosIds;

@Component
@RequiredArgsConstructor
public class OdsDosService {
    private final ItkProperties itkProperties;
    private final RestTemplate restTemplate;

    public OdsCodesDosIds fetchOdsCodeAndDosIds() {
        ResponseEntity<OdsCodesDosIds> response = restTemplate.getForEntity(itkProperties.getExternalConfigurationServiceUrl(),
            OdsCodesDosIds.class);
        return response.getBody();
    }
}
