package uk.nhs.adaptors.oneoneone.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.service.OdsDosService;

@Component
@AllArgsConstructor
public class ScheduledTasks {
    private final ItkProperties itkProperties;
    private final OdsDosService odsDosService;

    @Scheduled(fixedRateString="#{${itk.fetchIntervalMinutes} * 60 * 1000}")
    public void scheduleFetchingOdsCodeAndDosIds() {
        odsDosService.fetchOdsCodeAndDosIds();
        // set itkProperties
    }

}
