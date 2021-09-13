package uk.nhs.adaptors.oneoneone.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.service.OdsDosService;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnExpression(value = "!'${itk.externalConfigurationServiceUrl}'.empty")
public class OdsCodeDosIdsScheduledTask {
    private final ItkProperties itkProperties;
    private final OdsDosService odsDosService;

    @Scheduled(fixedRateString = "#{${itk.fetchIntervalMinutes} * 60 * 1000}")
    public void updateOdsCodeDosIdConfiguration() {
        LOGGER.info("Fetching ODS codes and DOS IDs from {}", itkProperties.getExternalConfigurationServiceUrl());
        OdsCodesDosIds odsCodesDosIds = odsDosService.fetchOdsCodeAndDosIds();
        itkProperties.setOdsCodesDosIds(odsCodesDosIds);
    }
}
