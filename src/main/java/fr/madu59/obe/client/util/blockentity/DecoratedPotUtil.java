package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedPotUtil {
    public static Identifier getDecoratedPotMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }

    public static ModelLayerLocation getDecoratedPotModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_BASE;
    }

    public static ModelLayerLocation getDecoratedPotSideModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_SIDES;
    }

    public static void transformDecoratedPot(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.mulPose(DecoratedPotRenderer.modelTransformation(facing));
    }
}
