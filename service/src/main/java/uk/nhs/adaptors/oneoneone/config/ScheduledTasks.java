package uk.nhs.adaptors.oneoneone.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledTasks { // todo nowa nazwa
    private final ItkProperties itkProperties;

    @Scheduled(fixedRateString="#{${itk.fetchIntervalMinutes} * 60 * 1000}")
    public void fetchOdsCodeAndDosIds() {
        itkProperties.getUrl();
    }

}
