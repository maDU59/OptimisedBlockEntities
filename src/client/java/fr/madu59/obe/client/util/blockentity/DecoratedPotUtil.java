package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedPotUtil {
    public static Identifier getMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_BASE;
    }

    public static ModelLayerLocation getSideModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_SIDES;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.pushPose();
        poseStack.translate((double)0.5F, (double)0.0F, (double)0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate((double)-0.5F, (double)0.0F, (double)-0.5F);
    }
}
