package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BellUtil extends AbstractUtil{
    public static Identifier getMaterial(BlockState state){
        return BellRenderer.BELL_TEXTURE.texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.BELL;
    }

    public static Identifier getMaterial(BlockState state, BlockEntity entity){
        return getMaterial(state);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity entity){
        return ModelLayers.BELL;
    }
}
