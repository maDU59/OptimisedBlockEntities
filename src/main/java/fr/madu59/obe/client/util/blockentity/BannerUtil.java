package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class BannerUtil {
    public static ResourceLocation getMaterial(BlockState state){
        return ResourceUtil.entityTextureFormatter(ModelBakery.BANNER_BASE.texture());
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.BANNER;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof WallBannerBlock) {
            float angle = -(state.getValue(WallBannerBlock.FACING)).toYRot();
            poseStack.translate(0.5F, -0.16666667F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.translate(0.0F, -0.3125F, -0.4375F);
        } else {
            float angle = -RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION));
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }
        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
    }
}
