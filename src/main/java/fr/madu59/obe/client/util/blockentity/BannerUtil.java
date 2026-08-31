package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerUtil {
    public static Identifier getMaterial(BlockState state){
        return Sheets.BANNER_BASE.texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        if(state.getBlock() instanceof WallBannerBlock)  return ModelLayers.WALL_BANNER;
        else return  ModelLayers.STANDING_BANNER;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof WallBannerBlock) {
            Direction facing = state.getValue(WallBannerBlock.FACING);
            poseStack.mulPose(BannerRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(BannerRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(BannerBlock.ROTATION)));
        }
    }
}
