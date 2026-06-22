package fr.madu59.obe.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HangingSignUtil {
    public static ResourceLocation getHangingSignMaterial(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        return Sheets.getHangingSignMaterial(woodType).texture();
    }

    public static ModelLayerLocation getHangingSignModelLayerLocation(BlockState state){
        return ModelLayers.createHangingSignModelName(SignBlock.getWoodType(state.getBlock()));
    }

    public static void transformHangingSign(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof SignBlock block) {
            poseStack.translate((double)0.5F, (double)0.9375F, (double)0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-block.getYRotationDegrees(state)));
            poseStack.translate(0.0F, -0.3125F, 0.0F);

            float f = 1f;
            poseStack.scale(f, -f, -f);
        }
    }
}
