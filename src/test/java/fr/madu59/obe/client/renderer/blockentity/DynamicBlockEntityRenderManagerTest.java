package fr.madu59.obe.client.renderer.blockentity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.api.render.DynamicBlockEntityRenderPredicate;

class DynamicBlockEntityRenderManagerTest {
    @Test
    void unregisteredTypeRendersNormally() {
        var registry = new DynamicBlockEntityRenderManager.DecisionRegistry<Object, Object>();

        assertTrue(registry.shouldRender(new Object(), new Object()));
    }

    @Test
    void registeredPredicateCanSkip() {
        var registry = new DynamicBlockEntityRenderManager.DecisionRegistry<Object, Object>();
        Object type = new Object();
        registry.register(type, ignored -> false);

        assertFalse(registry.shouldRender(type, new Object()));
    }

    @Test
    void predicateExceptionFailsOpen() {
        var registry = new DynamicBlockEntityRenderManager.DecisionRegistry<Object, Object>();
        Object type = new Object();
        registry.register(type, ignored -> {
            throw new IllegalStateException("boom");
        });

        assertTrue(registry.shouldRender(type, new Object()));
    }

    @Test
    void sodiumExtractionSkipIsCountedPerRegisteredType() {
        var registry = new DynamicBlockEntityRenderManager.DecisionRegistry<Object, Object>();
        Object type = new Object();
        registry.register(type, ignored -> false);
        long before = registry.preExtractionSkipCount(type);

        assertFalse(registry.shouldRenderBeforeExtraction(type, new Object()));
        assertEquals(before + 1, registry.preExtractionSkipCount(type));
    }

    @Test
    void decisionsDoNotRetainInvocationValues() throws IllegalAccessException {
        var registry = new DynamicBlockEntityRenderManager.DecisionRegistry<Object, Object>();
        Object type = new Object();
        Object blockEntity = new Object();
        registry.register(type, ignored -> false);

        assertFalse(registry.shouldRenderBeforeExtraction(type, blockEntity));

        for (var field : DynamicBlockEntityRenderManager.DecisionRegistry.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(registry) instanceof Map<?, ?> map) {
                assertFalse(map.containsKey(blockEntity));
                assertFalse(map.containsValue(blockEntity));
            }
        }
    }

    @Test
    void genericRegistryBytecodeDoesNotReferenceSophisticatedClasses() throws IOException {
        assertNoSophisticatedReference(DynamicBlockEntityRenderManager.class);
        assertNoSophisticatedReference(DynamicBlockEntityRenderPredicate.class);
    }

    private static void assertNoSophisticatedReference(Class<?> type) throws IOException {
        String resource = "/" + type.getName().replace('.', '/') + ".class";
        try (var input = type.getResourceAsStream(resource)) {
            assertTrue(input != null, "missing bytecode resource for " + type);
            String constants = new String(input.readAllBytes(), StandardCharsets.ISO_8859_1);
            assertFalse(constants.contains("sophisticatedstorage"));
            assertFalse(constants.contains("sophisticatedcore"));
        }
    }
}
