package fr.madu59.obe.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/** Optional local ABI matrix; absent external jars are deliberately not packaged. */
class SophisticatedStorageJarMatrixTest {
    private static final String CHEST_RENDERER =
            "net/p3pp3rf1y/sophisticatedstorage/client/render/ChestRenderer.class";
    private static final String CHEST_SUB_RENDERER =
            "net/p3pp3rf1y/sophisticatedstorage/client/render/ChestRenderer$ChestSubRenderer.class";
    private static final String SHULKER_RENDERER =
            "net/p3pp3rf1y/sophisticatedstorage/client/render/ShulkerBoxRenderer.class";
    private static final String SETTINGS_HANDLER =
            "net/p3pp3rf1y/sophisticatedstorage/settings/StorageSettingsHandler.class";
    private static final String CORE_SEVEN_ARGUMENT_CONSTRUCTOR =
            "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;"
                    + "Ljava/util/function/Consumer;IZLjava/util/function/Supplier;)V";

    @TestFactory
    Stream<DynamicTest> classifiesEveryAvailableValidatedJar() {
        Map<Path, SophisticatedStorageRendererCompatibility.ShulkerLayout> matrix = new LinkedHashMap<>();
        matrix.put(Path.of("C:/tmp/sophisticatedstorage-1.21.1-1.5.70.1941.jar"),
                SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.71.1949.jar", false);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.72.1956.jar", false);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.73.1960.jar", false);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.76.1972.jar", true);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.77.1982.jar", true);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.79.1991.jar", true);
        add(matrix, "sophisticatedstorage-1.21.1-1.5.80.1999.jar", true);

        return matrix.entrySet().stream()
                .filter(entry -> Files.isRegularFile(entry.getKey()))
                .map(entry -> DynamicTest.dynamicTest(entry.getKey().getFileName().toString(),
                        () -> assertCompatible(entry.getKey(), entry.getValue())));
    }

    private static void add(Map<Path, SophisticatedStorageRendererCompatibility.ShulkerLayout> matrix,
            String fileName, boolean extended) {
        matrix.put(Path.of("compat-test-jars", "sophisticatedstorage", fileName), extended
                ? SophisticatedStorageRendererCompatibility.ShulkerLayout.EXTENDED_SEVEN_ARGUMENT_BODY
                : SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY);
    }

    private static void assertCompatible(Path jarPath,
            SophisticatedStorageRendererCompatibility.ShulkerLayout expected) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            assertTrue(SophisticatedStorageRendererCompatibility.supportsChest(
                    read(jar, CHEST_RENDERER), read(jar, CHEST_SUB_RENDERER)),
                    () -> "Unsupported chest ABI in " + jarPath);
            assertEquals(expected, SophisticatedStorageRendererCompatibility.classifyShulker(
                    read(jar, SHULKER_RENDERER)), () -> "Unexpected shulker ABI in " + jarPath);
            boolean legacyStorageCallSite = expected
                    == SophisticatedStorageRendererCompatibility.ShulkerLayout.LEGACY_SIX_ARGUMENT_BODY;
            assertEquals(legacyStorageCallSite, SophisticatedStorageMixinPlugin.requiresConstructorBridge(
                    java.util.List.of(CORE_SEVEN_ARGUMENT_CONSTRUCTOR), read(jar, SETTINGS_HANDLER)),
                    () -> "Unexpected Core constructor call site in " + jarPath);
        }
    }

    private static ClassNode read(JarFile jar, String entryName) throws IOException {
        var entry = jar.getJarEntry(entryName);
        if (entry == null) {
            throw new IOException("Missing " + entryName + " in " + jar.getName());
        }
        try (InputStream input = jar.getInputStream(entry)) {
            ClassNode node = new ClassNode();
            new ClassReader(input).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }
}
