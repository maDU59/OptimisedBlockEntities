package fr.madu59.obe.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CompositeBlockStateModel implements BakedModel {
    private final BakedModel firstModel;
    private final BakedModel secondModel;

    public CompositeBlockStateModel(BakedModel firstModel, BakedModel secondModel) {
        this.firstModel = firstModel;
        this.secondModel = secondModel;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return firstModel.getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState arg0, Direction arg1, RandomSource arg2) {
        List<BakedQuad> output = new ArrayList<>();
        output.addAll(firstModel.getQuads(arg0, arg1, arg2));
        output.addAll(secondModel.getQuads(arg0, arg1, arg2));
        return output;
    }

    @Override
    public ItemTransforms getTransforms() {
        return firstModel.getTransforms();
    }

    @Override
    public boolean isGui3d() {
        return firstModel.isGui3d();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return firstModel.useAmbientOcclusion();
    }

    @Override
    public boolean usesBlockLight() {
        return firstModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return firstModel.isCustomRenderer();
    }

    @Override
    public ItemOverrides getOverrides() {
        return firstModel.getOverrides();
    }
}