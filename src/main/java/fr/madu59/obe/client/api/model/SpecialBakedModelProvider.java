package fr.madu59.obe.client.api.model;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Builds arbitrary, potentially layered terrain geometry for one block entity.
 *
 * @param <K> immutable appearance value used as the model cache key
 */
public interface SpecialBakedModelProvider<K> {
    /**
     * Resolves all state that affects static terrain geometry. Returning null is
     * a resolution failure for a registered provider, not a request to use a
     * lower-precedence provider.
     */
    @Nullable K resolveAppearance(SpecialBakedModelContext context) throws Exception;

    /**
     * Builds a model from the exact appearance object returned above.
     */
    BakedModel bake(K appearance, SpecialBakedModelContext context) throws Exception;

    /**
     * Render layers must be selected before OBE replaces the original block
     * model during section tessellation. Arbitrary entity-texture models use
     * non-mipped cutout by default; providers can opt into another layer set.
     */
    default ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }

    default boolean keepOriginalModel() {
        return false;
    }

    default boolean showOriginalWhenEntityRendered() {
        return false;
    }
}
