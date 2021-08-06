package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ResourceUtilTest {

    private static final String UUID_PATTERN = "^[a-zA-Z0-9]{8}([-\\s]?(?:[a-zA-Z0-9]{4})){3}?[-\\s]?(?:[a-zA-Z0-9]{12})?$";
    private static final Pattern PATTERN = Pattern.compile(UUID_PATTERN);

    public boolean verifyUUID(String uuid) {
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
}
