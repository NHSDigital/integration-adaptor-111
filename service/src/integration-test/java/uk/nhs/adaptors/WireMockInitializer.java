package uk.nhs.adaptors;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static uk.nhs.adaptors.TestResourceUtils.readResourceAsString;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String ODS_CODES_DOS_IDS_RESPONSE = readResourceAsString("/configuration/odsCodesDosIds.json");
    private static final String ODS_DOS_CONF_URI = "/configuration";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();
        configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        TestPropertyValues
            .of("itk.externalConfigurationServiceUrl:http://localhost:" + wireMockServer.port() + ODS_DOS_CONF_URI)
            .applyTo(configurableApplicationContext);

        stubOdsCodesDosIds(wireMockServer);
    }

    private void stubOdsCodesDosIds(WireMockServer wireMockServer) {
        wireMockServer.stubFor(
            WireMock.get(ODS_DOS_CONF_URI)
                .willReturn(aResponse()
                    .withStatus(OK.value())
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(ODS_CODES_DOS_IDS_RESPONSE)));
    }
}
