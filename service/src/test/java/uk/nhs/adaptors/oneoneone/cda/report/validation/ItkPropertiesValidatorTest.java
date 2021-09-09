package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.nhs.adaptors.oneoneone.config.ItkProperties;
import uk.nhs.adaptors.oneoneone.config.OdsCodesDosIds;

@ExtendWith(MockitoExtension.class)
public class ItkPropertiesValidatorTest {
    private static final String VALID_URL = "http://external-service.com/configuration";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ItkProperties itkProperties;

    @InjectMocks
    private ItkPropertiesValidator validator;

    @Test
    public void shouldFailWhenNoItkPropertyIsSet() {
        Errors errors = mock(Errors.class);

        validator.validate(itkProperties, errors);

        verify(errors).reject("1", "Configuration Error. At least one of PEM111_ITK_ODS_CODE_LIST, "
            + "PEM111_ITK_DOS_ID_LIST, PEM111_ITK_EXTERNAL_CONFIGURATION_URL itk properties has to be set.");
    }

    @Test
    public void shouldFailWhenCouldNotFetchConfigurationFromExternalService() {
        when(itkProperties.getExternalConfigurationServiceUrl()).thenReturn(VALID_URL);
        when(restTemplate.getForEntity(VALID_URL, OdsCodesDosIds.class)).thenThrow(RestClientException.class);
        Errors errors = mock(Errors.class);

        validator.validate(itkProperties, errors);

        verify(errors).reject("1", "Failed to retrieve configuration from external server: " + VALID_URL);
    }

    @Test
    public void shouldNotFailWhenValidUrlSet() {
        when(itkProperties.getExternalConfigurationServiceUrl()).thenReturn(VALID_URL);
        Errors errors = mock(Errors.class);

        validator.validate(itkProperties, errors);

        verifyNoInteractions(errors);
    }

    @Test
    public void shouldNotFailWhenOdsCodeListSet() {
        when(itkProperties.getOdsCodes()).thenReturn(newArrayList("EM328"));
        Errors errors = mock(Errors.class);

        validator.validate(itkProperties, errors);

        verifyNoInteractions(errors);
    }

    @Test
    public void shouldNotFailWhenDosIdListSet() {
        when(itkProperties.getDosIds()).thenReturn(newArrayList("9553453"));
        Errors errors = mock(Errors.class);

        validator.validate(itkProperties, errors);

        verifyNoInteractions(errors);
    }
}
