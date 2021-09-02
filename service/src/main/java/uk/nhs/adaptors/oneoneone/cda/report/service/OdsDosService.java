package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;

@Component
@AllArgsConstructor
public class OdsDosService {
    private final ItkProperties itkProperties;

    public void fetchOdsCodeAndDosIds() {
        WebClient webClient = WebClient.builder()
            .baseUrl(itkProperties.getUrl())
            .build();

        webClient.get()
            .uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve();
//            .bodyToMono(.class); // todo
    }
}
