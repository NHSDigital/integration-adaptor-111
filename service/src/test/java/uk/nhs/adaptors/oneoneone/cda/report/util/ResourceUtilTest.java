package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ResourceUtilTest {

    private static final String UUID_PATTERN = "^[a-zA-Z0-9]{8}([-\\s]?(?:[a-zA-Z0-9]{4})){3}?[-\\s]?(?:[a-zA-Z0-9]{12})?$";
    private static final Pattern PATTERN = Pattern.compile(UUID_PATTERN);
    private static final String PATIENT_ID = "1234567";

    private boolean verifyUUID(String uuid) {
        return PATTERN.matcher(uuid).matches();
    }

    @Mock
    private ResourceUtil resourceUtil;

    @BeforeEach
    public void setup() {
        resourceUtil = new ResourceUtil();
    }

    @Test
    public void shouldReturnTrueForUuidPattern() {
        assertTrue(verifyUUID(resourceUtil.newRandomUuid().toString()));
    }

    @Test
    public void shouldCreateReference() {
        Patient patient = new Patient();
        patient.setId(PATIENT_ID);

        Reference reference = resourceUtil.createReference(patient);

        assertThat(reference.getReferenceElement().getValue()).isEqualTo("urn:uuid:" + PATIENT_ID);
    }
}
