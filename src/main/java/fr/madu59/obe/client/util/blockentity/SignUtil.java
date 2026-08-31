package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.PlainSignBlock.Attachment;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignUtil {
    public static Identifier getMaterial(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        return Sheets.getSignSprite(woodType).texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        if (PlainSignBlock.getAttachmentPoint(state) == Attachment.WALL) {
            return ModelLayers.createWallSignModelName(woodType);
        }
        else{
            return ModelLayers.createStandingSignModelName(woodType);
        }
    }

    public static void transform(BlockState state, PoseStack poseStack){
        if (PlainSignBlock.getAttachmentPoint(state) == Attachment.WALL) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(StandingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
        }
        else {
            int rotationSegment = state.getValue(BlockStateProperties.ROTATION_16);
            poseStack.mulPose(StandingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
        }
    }
}
