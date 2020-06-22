package uk.nhs.adaptors.oneoneone.cda.report.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FhirStorageService {
    private final FhirContext context;

    @Value("${fhir.server}")
    private String fhirServer;

    @Value("${fhir.server.auth.token}")
    private String fhirServerAuthToken;

    public Reference create(Resource resource) {
        var id = client()
                .create()
                .resource(resource)
                .execute()
                .getId();
        resource.setId(id);
        return new Reference(id);
    }

    private IGenericClient client() {
        IGenericClient iGenericClient = context.newRestfulGenericClient(fhirServer);
        iGenericClient.registerInterceptor(new IClientInterceptor() {
            @Override
            public void interceptRequest(IHttpRequest theRequest) {
                theRequest.addHeader(HttpHeaders.AUTHORIZATION, fhirServerAuthToken);
            }

            @Override
            public void interceptResponse(IHttpResponse theResponse) throws IOException {

            }
        });

        return iGenericClient;
    }

    public Bundle getEncounterReport(Reference reference) {
        IIdType referenceElement = reference.getReferenceElement();
        String baseUrl = referenceElement.getBaseUrl();
        String encounterId = referenceElement.getIdPart();

        return context.newRestfulGenericClient(baseUrl)
                .search().forResource(Encounter.class)
                .where(Encounter.RES_ID.exactly().identifier(encounterId))
                .include(Encounter.INCLUDE_ALL)
                .revInclude(Encounter.INCLUDE_ALL)
                .returnBundle(Bundle.class)
                .execute();
    }
}
