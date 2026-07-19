package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxUtil extends AbstractUtil{
    public static Identifier getMaterial(BlockState state){
        if(state.getBlock() instanceof ShulkerBoxBlock block){
            DyeColor color = block.getColor();
            if (color == null) {
            return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            } else {
                return Sheets.getShulkerBoxSprite(color).texture();
            }
        }
        else return null;
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.SHULKER_BOX;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.mulPose(ShulkerBoxRenderer.modelTransform(facing));
    }
}
