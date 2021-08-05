package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.regex.Pattern;

public class ResourceUtilTest {
    private static final String UUID_PATTERN = "^[a-zA-Z0-9]{8}([-\\s]?(?:[a-zA-Z0-9]{4})){3}?[-\\s]?(?:[a-zA-Z0-9]{12})?$";
    private static final Pattern PATTERN = Pattern.compile(UUID_PATTERN);

    public static boolean verifyUUID(String uuid) {

        return PATTERN.matcher(uuid).matches();
    }
}
