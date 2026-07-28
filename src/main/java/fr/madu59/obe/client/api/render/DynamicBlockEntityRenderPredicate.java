package fr.madu59.obe.client.api.render;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Decides whether a block entity needs its dynamic renderer for the current
 * frame. Implementations must not retain the supplied block entity or its
 * level.
 *
 * @param <T> registered block-entity type
 * @since 1.1.37
 */
@FunctionalInterface
public interface DynamicBlockEntityRenderPredicate<T extends BlockEntity> {
    /**
     * @return {@code true} to execute the original BER, {@code false} to skip it
     */
    boolean shouldRenderDynamic(T blockEntity);
}
