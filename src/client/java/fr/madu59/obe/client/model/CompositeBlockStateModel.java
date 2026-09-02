package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeBlockStateModel implements BakedModel {
    private final BakedModel[] models;

    public CompositeBlockStateModel(BakedModel... models) {
        this.models = Arrays.stream(models)
            .filter(model -> model != null && model != this)
            .toArray(BakedModel[]::new);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return models[0].getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState arg0, Direction arg1, RandomSource arg2) {
        List<BakedQuad> output = new ArrayList<>();
        for(BakedModel model : models){
            output.addAll(model.getQuads(arg0, arg1, arg2));
        }
        return output;
    }

    @Override
    public ItemTransforms getTransforms() {
        return models[0].getTransforms();
    }

    @Override
    public boolean isGui3d() {
        return models[0].isGui3d();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return models[0].useAmbientOcclusion();
    }

    @Override
    public boolean usesBlockLight() {
        return models[0].usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return models[0].isCustomRenderer();
    }
}