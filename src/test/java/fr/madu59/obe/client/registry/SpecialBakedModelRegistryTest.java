package fr.madu59.obe.client.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.api.model.SpecialBakedModelContext;
import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import net.minecraft.client.resources.model.BakedModel;

class SpecialBakedModelRegistryTest {
    @AfterEach
    void clearRegistry() {
        SpecialBakedModelRegistry.clearForTests();
    }

    @Test
    void registersProvidersByBlockEntityTypeWithStableIdentity() {
        Object chestType = new Object();
        Object bannerType = new Object();
        TestProvider firstProvider = new TestProvider();
        var first = SpecialBakedModelRegistry.registerForTests(chestType, firstProvider);

        assertSame(firstProvider, first.provider());
        assertSame(first, SpecialBakedModelRegistry.getForTests(chestType).orElseThrow());
        assertTrue(SpecialBakedModelRegistry.getForTests(bannerType).isEmpty());
        assertEquals(first.identity(), SpecialBakedModelRegistry.getForTests(chestType).orElseThrow().identity());
    }

    @Test
    void replacingARegistrationChangesItsProviderIdentity() {
        Object chestType = new Object();
        var first = SpecialBakedModelRegistry.registerForTests(chestType, new TestProvider());
        var second = SpecialBakedModelRegistry.registerForTests(chestType, new TestProvider());

        assertNotEquals(first.identity(), second.identity());
        assertSame(second, SpecialBakedModelRegistry.getForTests(chestType).orElseThrow());
    }

    private static final class TestProvider implements SpecialBakedModelProvider<String> {
        @Override
        public String resolveAppearance(SpecialBakedModelContext context) {
            return "appearance";
        }

        @Override
        public BakedModel bake(String appearance, SpecialBakedModelContext context) {
            return context.originalModel();
        }
    }
}
