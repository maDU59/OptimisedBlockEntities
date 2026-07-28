package fr.madu59.obe.client.api.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Inputs supplied while resolving a per-block-entity terrain model.
 *
 * <p>Providers must not retain this context: it contains the live block entity.
 * Only the immutable appearance returned by the provider may be used as a
 * long-lived cache key.</p>
 */
public record SpecialBakedModelContext(
        BlockState state,
        BlockEntity blockEntity,
        BakedModel originalModel,
        TextureAtlasSprite particleSprite
) {}
