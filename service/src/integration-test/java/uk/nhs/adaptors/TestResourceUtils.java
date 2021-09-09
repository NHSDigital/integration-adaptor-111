package uk.nhs.adaptors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;

public class TestResourceUtils {
    @SneakyThrows
    public static String readResourceAsString(String path) {
        URL resource = WireMockInitializer.class.getResource(path);
        return Files.readString(Paths.get(resource.getPath()), UTF_8);
    }
}
