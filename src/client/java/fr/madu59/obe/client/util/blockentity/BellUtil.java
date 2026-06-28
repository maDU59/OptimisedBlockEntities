package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BellUtil {
    public static Identifier getBellMaterial(BlockState state){
        return BellRenderer.BELL_TEXTURE.texture();
    }

    public static Identifier getBellMaterial(BlockState state, BlockEntity be){
        return getBellMaterial(state);
    }

    public static ModelLayerLocation getBellModelLayerLocation(BlockState state){
        return ModelLayers.BELL;
    }

    public static ModelLayerLocation getBellModelLayerLocation(BlockState state, BlockEntity be){
        return getBellModelLayerLocation(state);
    }

    public static void transformBell(BlockState state, PoseStack poseStack){
        
    }

    public static void transformBell(BlockState state, BlockEntity be, PoseStack poseStack){
        
    }
}
