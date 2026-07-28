package fr.madu59.obe.client.resources;

import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/** Global bounded cache for arbitrary per-appearance terrain models. */
public final class SpecialBakedModelCache {
    public static final int DEFAULT_MAXIMUM_SIZE = 4096;

    private static final AtomicLong GENERATION = new AtomicLong();
    private static final BoundedModelCache<Key, BakedModel> CACHE =
            new BoundedModelCache<>(DEFAULT_MAXIMUM_SIZE);

    private SpecialBakedModelCache() {}

    public static BakedModel getOrBake(
            int providerIdentity,
            ResourceLocation blockEntityTypeId,
            BlockState state,
            Object appearance,
            BoundedModelCache.Factory<BakedModel> factory
    ) throws Exception {
        return CACHE.getOrCompute(
                new Key(providerIdentity, blockEntityTypeId, state, appearance, GENERATION.get()),
                factory
        );
    }

    public static long generation() {
        return GENERATION.get();
    }

    public static void clearForReload() {
        CACHE.clear();
        GENERATION.incrementAndGet();
    }

    public static BoundedModelCache.Stats stats() {
        return CACHE.stats();
    }

    public record Key(
            int providerIdentity,
            ResourceLocation blockEntityTypeId,
            BlockState blockState,
            Object appearance,
            long resourceGeneration
    ) {}
}
