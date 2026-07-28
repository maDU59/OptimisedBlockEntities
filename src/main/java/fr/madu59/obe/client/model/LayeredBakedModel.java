package fr.madu59.obe.client.model;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Immutable ordered collection of entity-model material passes exposed as terrain quads. */
public final class LayeredBakedModel implements BakedModel {
    private final List<Layer> layers;
    private final List<BakedQuad> quads;
    private final TextureAtlasSprite particleSprite;
    private final boolean ambientOcclusion;

    public LayeredBakedModel(List<Layer> layers, TextureAtlasSprite particleSprite, boolean ambientOcclusion) {
        this.layers = List.copyOf(layers);
        List<BakedQuad> combined = new ArrayList<>();
        this.layers.forEach(layer -> combined.addAll(layer.quads()));
        quads = List.copyOf(combined);
        this.particleSprite = particleSprite;
        this.ambientOcclusion = ambientOcclusion;
    }

    public List<Layer> layers() {
        return layers;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return side == null ? quads : List.of();
    }

    @Override public boolean useAmbientOcclusion() { return ambientOcclusion; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return particleSprite; }
    @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
    @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        // These are entity textures. Their transparent UV islands are not authored
        // for block-atlas mip generation, so the original renderer's non-mipped
        // cutout sampling must be preserved after stitching.
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }

    public record Layer(ResourceLocation material, List<BakedQuad> quads) {
        public Layer {
            quads = List.copyOf(quads);
        }
    }
}
