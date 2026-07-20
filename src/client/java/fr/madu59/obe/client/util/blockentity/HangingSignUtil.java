package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HangingSignUtil {
    public static ResourceLocation getMaterial(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        return Sheets.getHangingSignMaterial(woodType).texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.createHangingSignModelName(SignBlock.getWoodType(state.getBlock()), HangingSignRenderer.AttachmentType.byBlockState(state));
    }

    public static void transform(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof SignBlock block) {
            HangingSignRenderer.translateBase(poseStack, -block.getYRotationDegrees(state));

            float f = 1f;
            poseStack.scale(f, -f, -f);
        }
    }
}
