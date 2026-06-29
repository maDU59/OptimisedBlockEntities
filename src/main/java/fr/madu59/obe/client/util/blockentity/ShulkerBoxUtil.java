package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxUtil {
    public static ResourceLocation getShulkerBoxMaterial(BlockState state){
        if(state.getBlock() instanceof ShulkerBoxBlock block){
            DyeColor color = block.getColor();
            if (color == null) {
            return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            } else {
                return Sheets.SHULKER_TEXTURE_LOCATION.get(color.getId()).texture();
            }
        }
        else return null;
    }

    public static ModelLayerLocation getShulkerBoxModelLayerLocation(BlockState state){
        return ModelLayers.SHULKER;
    }

    public static void transformShulkerBox(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float g = 0.9995F;
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(facing.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);
    }
}
