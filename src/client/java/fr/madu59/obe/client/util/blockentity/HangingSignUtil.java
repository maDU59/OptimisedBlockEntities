package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HangingSignUtil {
    public static Identifier getHangingSignMaterial(BlockState state){
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        return Sheets.getHangingSignSprite(woodType).texture();
    }

    public static ModelLayerLocation getHangingSignModelLayerLocation(BlockState state){
        return ModelLayers.createHangingSignModelName(SignBlock.getWoodType(state.getBlock()), HangingSignBlock.getAttachmentPoint(state));
    }

    public static void transformHangingSign(BlockState state, PoseStack poseStack){
        if (state.hasProperty(CeilingHangingSignBlock.ATTACHED)) {
            int rotationSegment = state.getValue(BlockStateProperties.ROTATION_16);
            poseStack.mulPose(HangingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
        }
        else {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(HangingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
        }
    }
}
