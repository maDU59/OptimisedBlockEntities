package fr.madu59.obe.client.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

class SpecialBakedModelCacheTest {
    @Test
    void providerIdentityIsolatesModelsAndReloadGenerationInvalidatesThem() throws Exception {
        // A null state is sufficient here; bootstrapping vanilla registries is outside this unit test's scope.
        net.minecraft.world.level.block.state.BlockState state = null;
        ResourceLocation type = ResourceLocation.parse("test:type");
        AtomicInteger builds = new AtomicInteger();

        BakedModel first = SpecialBakedModelCache.getOrBake(10, type, state, "same", () -> model(builds));
        BakedModel hit = SpecialBakedModelCache.getOrBake(10, type, state, "same", () -> model(builds));
        BakedModel otherProvider = SpecialBakedModelCache.getOrBake(11, type, state, "same", () -> model(builds));

        assertSame(first, hit);
        assertNotSame(first, otherProvider);
        assertEquals(2, builds.get());

        long generation = SpecialBakedModelCache.generation();
        SpecialBakedModelCache.clearForReload();
        BakedModel afterReload = SpecialBakedModelCache.getOrBake(10, type, state, "same", () -> model(builds));
        assertNotSame(first, afterReload);
        assertEquals(generation + 1, SpecialBakedModelCache.generation());
        assertEquals(3, builds.get());
    }

    private static BakedModel model(AtomicInteger builds) {
        builds.incrementAndGet();
        return mock(BakedModel.class);
    }
}
