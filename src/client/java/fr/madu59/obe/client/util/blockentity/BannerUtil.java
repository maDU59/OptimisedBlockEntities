package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class BannerUtil {
    public static Identifier getBannerMaterial(BlockState state){
        return ResourceUtil.entityTextureFormatter(ModelBakery.BANNER_BASE.texture());
    }

    public static ModelLayerLocation getBannerModelLayerLocation(BlockState state){
        if(state.getBlock() instanceof WallBannerBlock)  return ModelLayers.WALL_BANNER;
        else return  ModelLayers.STANDING_BANNER;
    }

    public static void transformBanner(BlockState state, PoseStack poseStack){
        float angle;
        if (state.getBlock() instanceof WallBannerBlock) {
            angle = -(state.getValue(WallBannerBlock.FACING)).toYRot();
        } else {
            angle = -RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION));
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
    }
}
