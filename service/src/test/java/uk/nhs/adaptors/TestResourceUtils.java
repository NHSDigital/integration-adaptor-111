package uk.nhs.adaptors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readString;

import java.net.URL;
import java.nio.file.Paths;

import lombok.SneakyThrows;

public class TestResourceUtils {
    @SneakyThrows
    public static String readResourceAsString(String path) {
        URL resource = TestResourceUtils.class.getResource(path);
        return readString(Paths.get(resource.getPath()), UTF_8);
    }
}
