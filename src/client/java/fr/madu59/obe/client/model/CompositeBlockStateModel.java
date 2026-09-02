package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.util.RandomSource;

import java.util.Arrays;
import java.util.List;

public class CompositeBlockStateModel implements BlockStateModel {
    private final BlockStateModel[] models;

    public CompositeBlockStateModel(BlockStateModel... models) {
        this.models = Arrays.stream(models)
            .filter(model -> model != null && model != this)
            .toArray(BlockStateModel[]::new);
    }

    @Override
    public void collectParts(RandomSource random, List<BlockModelPart> output) {
        for(BlockStateModel model : models){
            model.collectParts(random, output);
        }
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return models[0].particleIcon();
    }
}