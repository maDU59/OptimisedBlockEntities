package fr.madu59.obe.client.renderer.blockentity;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;

import fr.madu59.obe.client.api.render.DynamicBlockEntityRenderPredicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Client-only, per-frame BER decisions kept separate from remesh-stable skips. */
public final class DynamicBlockEntityRenderManager {
    private static final DecisionRegistry<BlockEntityType<?>, BlockEntity> REGISTRY = new DecisionRegistry<>();

    private DynamicBlockEntityRenderManager() {
    }

    public static <T extends BlockEntity> void register(
            BlockEntityType<T> type, DynamicBlockEntityRenderPredicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        REGISTRY.register(Objects.requireNonNull(type, "type"),
                blockEntity -> invoke(predicate, blockEntity));
    }

    /** Missing predicates and predicate failures deliberately render normally. */
    public static boolean shouldRender(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return true;
        }
        return REGISTRY.shouldRender(blockEntity.getType(), blockEntity);
    }

    /** Sodium entry point, evaluated before renderer lookup and extraction. */
    public static boolean shouldRenderBeforeExtraction(BlockEntity blockEntity) {
        return blockEntity == null
                || REGISTRY.shouldRenderBeforeExtraction(blockEntity.getType(), blockEntity);
    }

    public static long preExtractionSkipCount(BlockEntityType<?> type) {
        return REGISTRY.preExtractionSkipCount(type);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> boolean invoke(
            DynamicBlockEntityRenderPredicate<?> predicate, T blockEntity) {
        return ((DynamicBlockEntityRenderPredicate<T>) predicate).shouldRenderDynamic(blockEntity);
    }

    /** Pure registry core, split out so fail-open behavior is testable without bootstrapping Minecraft. */
    static final class DecisionRegistry<K, V> {
        private final ConcurrentMap<K, Predicate<? super V>> predicates = new ConcurrentHashMap<>();
        private final ConcurrentMap<K, LongAdder> preExtractionSkips = new ConcurrentHashMap<>();

        void register(K key, Predicate<? super V> predicate) {
            predicates.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(predicate, "predicate"));
        }

        boolean shouldRender(K key, V value) {
            Predicate<? super V> predicate = predicates.get(key);
            if (predicate == null) {
                return true;
            }
            try {
                return predicate.test(value);
            } catch (Exception ignored) {
                return true;
            }
        }

        boolean shouldRenderBeforeExtraction(K key, V value) {
            boolean shouldRender = shouldRender(key, value);
            if (!shouldRender) {
                preExtractionSkips.computeIfAbsent(key, ignored -> new LongAdder()).increment();
            }
            return shouldRender;
        }

        long preExtractionSkipCount(K key) {
            LongAdder counter = preExtractionSkips.get(key);
            return counter == null ? 0 : counter.sum();
        }
    }
}
