package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags;
import net.minecraft.client.resources.model.sprite.Material;
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
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        for(BlockStateModel model : models){
            model.collectParts(random, output);
        }
    }

    @Override
    public @MaterialFlags int materialFlags() {
        return models[0].materialFlags();
    }

    @Override
    public @MaterialFlags Material.Baked particleMaterial() {
        return models[0].particleMaterial();
    }
}