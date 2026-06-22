package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignUtil {
    public static ResourceLocation getSignMaterial(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        return Sheets.getSignMaterial(woodType).texture();
    }

    public static ModelLayerLocation getSignModelLayerLocation(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        if (state.getBlock() instanceof SignBlock block) {
            if(block instanceof WallSignBlock){
                return ModelLayers.createWallSignModelName(woodType);
            }
            else{
                return ModelLayers.createStandingSignModelName(woodType);
            }
        }
        else return null;
    }

    public static void transformSign(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof SignBlock block) {
            SignRenderer.translateBase(poseStack, -block.getYRotationDegrees(state));
            if (block instanceof WallSignBlock) {
                poseStack.translate(0.0F, -0.3125F, -0.4375F);
            }

            float f = 0.6666667F;
            poseStack.scale(f, -f, -f);
        }
    }
}