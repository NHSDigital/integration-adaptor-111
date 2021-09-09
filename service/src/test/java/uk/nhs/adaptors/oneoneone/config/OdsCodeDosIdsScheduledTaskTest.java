package uk.nhs.adaptors.oneoneone.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.service.OdsDosService;

@ExtendWith(MockitoExtension.class)
public class OdsCodeDosIdsScheduledTaskTest {
    @Mock
    private ItkProperties itkProperties;
    @Mock
    private OdsDosService odsDosService;
    @InjectMocks
    private OdsCodeDosIdsScheduledTask task;

    @Test
    public void shouldUpdateOdsCodeDosIdConfiguration() {
        OdsCodesDosIds odsCodesDosIds = mock(OdsCodesDosIds.class);
        when(odsDosService.fetchOdsCodeAndDosIds()).thenReturn(odsCodesDosIds);

        task.updateOdsCodeDosIdConfiguration();

        verify(itkProperties).setOdsCodesDosIds(odsCodesDosIds);
    }
}
