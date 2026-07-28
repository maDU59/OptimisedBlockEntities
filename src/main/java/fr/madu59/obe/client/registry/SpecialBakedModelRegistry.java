package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Registry for the arbitrary baked-model API. */
public final class SpecialBakedModelRegistry {
    private static final AtomicInteger NEXT_IDENTITY = new AtomicInteger(1);
    private static final Map<Object, Registration<?>> REGISTRATIONS = new ConcurrentHashMap<>();

    private SpecialBakedModelRegistry() {}

    public static <K> Registration<K> register(BlockEntityType<?> type, SpecialBakedModelProvider<K> provider) {
        return registerInternal(type, type, provider);
    }

    public static Optional<Registration<?>> get(BlockEntityType<?> type) {
        return Optional.ofNullable(REGISTRATIONS.get(type));
    }

    static void clearForTests() {
        REGISTRATIONS.clear();
        NEXT_IDENTITY.set(1);
    }

    static <K> Registration<K> registerForTests(Object key, SpecialBakedModelProvider<K> provider) {
        return registerInternal(key, null, provider);
    }

    static Optional<Registration<?>> getForTests(Object key) {
        return Optional.ofNullable(REGISTRATIONS.get(key));
    }

    private static <K> Registration<K> registerInternal(
            Object key,
            BlockEntityType<?> type,
            SpecialBakedModelProvider<K> provider
    ) {
        Registration<K> registration = new Registration<>(NEXT_IDENTITY.getAndIncrement(), type, provider);
        REGISTRATIONS.put(key, registration);
        return registration;
    }

    public record Registration<K>(
            int identity,
            BlockEntityType<?> blockEntityType,
            SpecialBakedModelProvider<K> provider
    ) {}
}
