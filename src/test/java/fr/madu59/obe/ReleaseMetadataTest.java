package fr.madu59.obe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ReleaseMetadataTest {
    private static final Pattern DEPENDENCY_TABLE_HEADER = Pattern.compile("^\\s*\\[\\[dependencies\\.[^]]+]]\\s*(?:#.*)?$");
    private static final Pattern STRING_PROPERTY = Pattern.compile("^\\s*([A-Za-z][A-Za-z0-9_]*)\\s*=\\s*\"([^\"]*)\"\\s*(?:#.*)?$");

    @Test
    void sophisticatedDependenciesKeepTheirApprovedBoundaries() throws IOException {
        String metadata = Files.readString(Path.of("src/main/resources/META-INF/neoforge.mods.toml"));

        Map<String, String> storageFields = dependencyFields(metadata, "sophisticatedstorage");
        assertEquals("optional", storageFields.get("type"), "sophisticatedstorage dependency field type");
        assertEquals("CLIENT", storageFields.get("side"), "sophisticatedstorage dependency field side");
        assertEquals("[1.5.70,)", storageFields.get("versionRange"), "sophisticatedstorage dependency field versionRange");

        Map<String, String> coreFields = dependencyFields(metadata, "sophisticatedcore");
        assertEquals("optional", coreFields.get("type"), "sophisticatedcore dependency field type");
        assertEquals("CLIENT", coreFields.get("side"), "sophisticatedcore dependency field side");
        assertEquals("[1.4.67,)", coreFields.get("versionRange"), "sophisticatedcore dependency field versionRange");
    }

    @Test
    void releaseVersionIsOnePointOnePointThirtySeven() throws IOException {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(Path.of("gradle.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        assertEquals("1.1.37", properties.getProperty("mod_version"));
    }

    private static Map<String, String> dependencyFields(String metadata, String modId) {
        Map<String, String> currentFields = null;

        for (String line : metadata.split("\\R")) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;
            }

            if (trimmedLine.startsWith("[[")) {
                if (currentFields != null && modId.equals(currentFields.get("modId"))) {
                    return currentFields;
                }
                currentFields = DEPENDENCY_TABLE_HEADER.matcher(line).matches() ? new LinkedHashMap<>() : null;
                continue;
            }

            if (currentFields != null) {
                Matcher property = STRING_PROPERTY.matcher(line);
                if (property.matches()) {
                    currentFields.put(property.group(1), property.group(2));
                }
            }
        }

        if (currentFields != null && modId.equals(currentFields.get("modId"))) {
            return currentFields;
        }

        throw new AssertionError("Missing dependency table for " + modId);
    }
}
