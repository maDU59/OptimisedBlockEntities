package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialFlags;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import java.util.List;

public class CompositeBlockStateModel implements BlockStateModel {
    private final BlockStateModel firstModel;
    private final BlockStateModel secondModel;

    public CompositeBlockStateModel(BlockStateModel firstModel, BlockStateModel secondModel) {
        this.firstModel = firstModel;
        this.secondModel = secondModel;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        firstModel.collectParts(random, output);
        secondModel.collectParts(random, output);
    }

    @Override
    public @MaterialFlags int materialFlags() {
        return firstModel.materialFlags();
    }

    @Override
    public @MaterialFlags Material.Baked particleMaterial() {
        return firstModel.particleMaterial();
    }
}