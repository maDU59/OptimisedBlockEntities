package fr.madu59.obe.client.model;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.model.BlockModelPart;
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
    public void collectParts(RandomSource random, List<BlockModelPart> output) {
        firstModel.collectParts(random, output);
        secondModel.collectParts(random, output);
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return firstModel.particleIcon();
    }
}